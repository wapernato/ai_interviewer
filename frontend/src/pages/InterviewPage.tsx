import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery } from "@tanstack/react-query";
import { Bot, MessageSquareText, RefreshCw, Send, Sparkles, UserRound } from "lucide-react";
import { useForm, useWatch } from "react-hook-form";
import { z } from "zod";
import { generateQuestion, submitAnswer } from "../api/interviewApi";
import { getTopics } from "../api/topicsApi";
import { Alert } from "../components/Alert";
import { EmptyState } from "../components/EmptyState";
import { LoadingState } from "../components/LoadingState";
import { PageHeader } from "../components/PageHeader";
import { useAuth } from "../features/auth/useAuth";
import { useApiErrorMessage } from "../hooks/useApiErrorMessage";

const questionSchema = z.object({
  topic: z.string().trim().min(2, "Выбери тему."),
});

const answerSchema = z.object({
  textAnswer: z.string().trim().min(1, "Ответ не может быть пустым.").max(3000, "Максимум 3000 символов."),
});

type QuestionFormValues = z.infer<typeof questionSchema>;
type AnswerFormValues = z.infer<typeof answerSchema>;

export function InterviewPage() {
  const { auth } = useAuth();
  const topicsQuery = useQuery({ queryKey: ["topics"], queryFn: getTopics });
  const questionForm = useForm<QuestionFormValues>({
    resolver: zodResolver(questionSchema),
    defaultValues: { topic: "" },
  });
  const answerForm = useForm<AnswerFormValues>({
    resolver: zodResolver(answerSchema),
    defaultValues: { textAnswer: "" },
  });
  const answerLength = (
    useWatch({
      control: answerForm.control,
      name: "textAnswer",
    }) ?? ""
  ).length;
  const selectedTopic =
    useWatch({
      control: questionForm.control,
      name: "topic",
    }) ?? "";

  const questionMutation = useMutation({
    mutationFn: generateQuestion,
    onSuccess: () => {
      answerForm.reset({ textAnswer: "" });
    },
  });

  const answerMutation = useMutation({
    mutationFn: submitAnswer,
  });

  const question = questionMutation.data;
  const errorMessage = useApiErrorMessage(topicsQuery.error || questionMutation.error || answerMutation.error);

  function handleGenerate(values: QuestionFormValues) {
    answerMutation.reset();
    questionMutation.mutate({ topic: values.topic });
  }

  function handleAnswer(values: AnswerFormValues) {
    if (!auth || !question) {
      return;
    }

    answerMutation.mutate({
      userId: auth.id,
      questionId: question.questionId,
      textAnswer: values.textAnswer,
    });
  }

  return (
    <section className="page-enter interview-page">
      <PageHeader
        actions={<span className="status-badge"><span /> Сессия активна</span>}
        eyebrow="Тренировка"
        title="Интервью"
        description="Формулируйте ответ как на реальном техническом собеседовании."
      />

      {errorMessage ? <Alert type="error">{errorMessage}</Alert> : null}

      <div className="interview-layout">
        <section className="panel interview-chat">
          <div className="chat-header">
            <div>
              <span className="panel-icon"><MessageSquareText aria-hidden="true" size={19} /></span>
              <div>
                <h2>Диалог</h2>
                <p>Ваш ответ и разбор интервьюера</p>
              </div>
            </div>
            <span className="chat-status"><span /> online</span>
          </div>

          <div className="chat-thread" aria-live="polite">
            {!question ? (
              <div className="chat-empty">
                <span><Sparkles aria-hidden="true" size={23} /></span>
                <strong>Начните с выбора темы</strong>
                <p>Вопрос появится здесь, а поле ответа станет доступным.</p>
              </div>
            ) : (
              <div className="chat-message assistant-message">
                <span className="message-avatar"><Bot aria-hidden="true" size={17} /></span>
                <div>
                  <small>AI Interviewer</small>
                  <p>{question.questionText}</p>
                </div>
              </div>
            )}

            {answerMutation.variables ? (
              <div className="chat-message user-message">
                <span className="message-avatar"><UserRound aria-hidden="true" size={17} /></span>
                <div>
                  <small>Вы</small>
                  <p>{answerMutation.variables.textAnswer}</p>
                </div>
              </div>
            ) : null}

            {answerMutation.data ? (
              <div className="chat-message assistant-message feedback-message">
                <span className="message-avatar"><Bot aria-hidden="true" size={17} /></span>
                <div>
                  <small>Разбор ответа</small>
                  <p>{answerMutation.data.feedback}</p>
                </div>
              </div>
            ) : null}

            {answerMutation.isPending ? (
              <div className="chat-message assistant-message">
                <span className="message-avatar"><Bot aria-hidden="true" size={17} /></span>
                <div>
                  <small>AI Interviewer</small>
                  <p className="typing-indicator"><span /><span /><span /></p>
                </div>
              </div>
            ) : null}
          </div>

          <form className="chat-composer" onSubmit={answerForm.handleSubmit(handleAnswer)}>
            <label className="visually-hidden" htmlFor="interview-answer">Текст ответа</label>
            <textarea
              disabled={!question || answerMutation.isPending}
              id="interview-answer"
              placeholder={question ? "Сформулируйте ответ..." : "Сначала выберите тему и получите вопрос"}
              rows={5}
              {...answerForm.register("textAnswer")}
            />
            {answerForm.formState.errors.textAnswer ? (
              <span className="field-error">{answerForm.formState.errors.textAnswer.message}</span>
            ) : null}
            <div className="composer-actions">
              <span>{answerLength} / 3000</span>
              <button className="primary-button" disabled={!question || answerMutation.isPending} type="submit">
                <Send aria-hidden="true" size={17} />
                {answerMutation.isPending ? "Отправка..." : "Отправить"}
              </button>
            </div>
          </form>
        </section>

        <aside className="interview-side">
          <section className="panel session-setup">
            <div className="panel-heading">
              <div>
                <span className="eyebrow">Параметры</span>
                <h2>Тема интервью</h2>
              </div>
              <button
                aria-label="Сгенерировать новый вопрос"
                className={`icon-button refresh-button ${questionMutation.isPending ? "is-spinning" : ""}`}
                disabled={!selectedTopic || questionMutation.isPending}
                onClick={() => void questionForm.handleSubmit(handleGenerate)()}
                title="Сгенерировать новый вопрос"
                type="button"
              >
                <RefreshCw aria-hidden="true" size={19} />
              </button>
            </div>
          {topicsQuery.isLoading ? <LoadingState /> : null}
          {topicsQuery.data?.length === 0 ? <EmptyState title="Темы не найдены" /> : null}
          <form className="stack-form" onSubmit={questionForm.handleSubmit(handleGenerate)}>
            <label>
              Тема
              <select {...questionForm.register("topic")} disabled={questionMutation.isPending}>
                <option value="">Выбери тему</option>
                {topicsQuery.data?.map((topic) => (
                  <option key={topic.id} value={topic.name}>
                    {topic.name}
                  </option>
                ))}
              </select>
              {questionForm.formState.errors.topic ? (
                <span className="field-error">{questionForm.formState.errors.topic.message}</span>
              ) : null}
            </label>
            <button className="primary-button" disabled={questionMutation.isPending} type="submit">
              <Sparkles aria-hidden="true" size={17} />
              {questionMutation.isPending ? "Генерация..." : question ? "Следующий вопрос" : "Получить вопрос"}
            </button>
          </form>
          </section>

          <section className="panel current-question">
            <div className="panel-heading">
              <div>
                <span className="eyebrow">Текущий вопрос</span>
                <h2>{question?.topicName ?? "Ожидает выбора"}</h2>
              </div>
              <span className="difficulty-badge">{question?.difficulty ?? "—"}</span>
            </div>
            {question ? (
              <p>{question.questionText}</p>
            ) : (
              <div className="question-placeholder">
                <Bot aria-hidden="true" size={24} />
                <span>Выберите тему, чтобы начать сессию.</span>
              </div>
            )}
          </section>
        </aside>
      </div>
    </section>
  );
}
