import { useEffect, useMemo, useState } from "react";
import heroImage from "./assets/hero.png";
import "./App.css";

const API_URL = "http://localhost:8080";

async function readResponse(response) {
    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }

    return await response.text();
}

function ApiNotice({ type, text }) {
    if (!text) {
        return null;
    }

    return (
        <div className={`api-notice ${type}`}>
            <span className="notice-dot"></span>
            <span>{text}</span>
        </div>
    );
}

function AppHeader({ apiStatus, usersCount, topicsCount, activeProfile }) {
    return (
        <header className="app-header">
            <div className="header-copy">
                <div className="product-mark">
                    <img src={heroImage} alt="" />
                    <div>
                        <span>AI Interviewer</span>
                        <strong>Java Backend Trainer</strong>
                    </div>
                </div>

                <h1>Рабочий экран собеседования</h1>
                <p>
                    Здесь видно весь backend-сценарий: пользователь и тема, генерация вопроса,
                    отправка ответа, feedback и история из PostgreSQL.
                </p>
            </div>

            <div className="header-status">
                <div className={`health-card ${apiStatus.ok ? "online" : "offline"}`}>
                    <span>Backend</span>
                    <strong>{apiStatus.label}</strong>
                </div>

                <div className="header-metrics">
                    <div>
                        <span>Users</span>
                        <strong>{usersCount}</strong>
                    </div>
                    <div>
                        <span>Topics</span>
                        <strong>{topicsCount}</strong>
                    </div>
                    <div>
                        <span>AI Profile</span>
                        <strong>{activeProfile ? "Active" : "Missing"}</strong>
                    </div>
                </div>
            </div>
        </header>
    );
}

function WorkflowStepper({ question, feedback }) {
    const steps = [
        { label: "Настройки", active: true },
        { label: "Вопрос", active: Boolean(question) },
        { label: "Ответ", active: Boolean(feedback) },
        { label: "История", active: false }
    ];

    return (
        <div className="workflow-stepper">
            {steps.map((step, index) => (
                <div className={`workflow-step ${step.active ? "active" : ""}`} key={step.label}>
                    <span>{index + 1}</span>
                    <strong>{step.label}</strong>
                </div>
            ))}
        </div>
    );
}

function InterviewSettings({
    userId,
    topicId,
    users,
    topics,
    selectedUser,
    selectedTopic,
    setUserId,
    setTopicId,
    onGenerateQuestion,
    loading
}) {
    return (
        <section className="surface settings-surface">
            <div className="surface-header">
                <div>
                    <span className="section-kicker">Interview setup</span>
                    <h2>Параметры сессии</h2>
                </div>
                <span className="api-chip">POST /api/interview/question</span>
            </div>

            <div className="form-grid">
                <label>
                    Пользователь
                    <select
                        value={userId}
                        onChange={(event) => setUserId(event.target.value)}
                        disabled={loading || users.length === 0}
                    >
                        {users.length === 0 && (
                            <option value="">Пользователи не загружены</option>
                        )}

                        {users.map((user) => (
                            <option key={user.id} value={user.id}>
                                {user.username} · ID {user.id}
                            </option>
                        ))}
                    </select>
                </label>

                <label>
                    Тема
                    <select
                        value={topicId}
                        onChange={(event) => setTopicId(event.target.value)}
                        disabled={loading || topics.length === 0}
                    >
                        {topics.length === 0 && (
                            <option value="">Темы не загружены</option>
                        )}

                        {topics.map((topic) => (
                            <option key={topic.id} value={topic.id}>
                                {topic.name} · ID {topic.id}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            <div className="selected-context">
                <div>
                    <span>Current user</span>
                    <strong>{selectedUser?.username || "Не выбран"}</strong>
                </div>
                <div>
                    <span>Current topic</span>
                    <strong>{selectedTopic?.name || "Не выбрана"}</strong>
                </div>
            </div>

            <button
                className="primary-button"
                onClick={onGenerateQuestion}
                disabled={loading || users.length === 0 || topics.length === 0}
            >
                {loading ? "Генерируем вопрос" : "Сгенерировать вопрос"}
            </button>
        </section>
    );
}

function QuestionCard({ question }) {
    return (
        <section className="surface question-surface">
            <div className="surface-header">
                <div>
                    <span className="section-kicker">Generated question</span>
                    <h2>{question ? "Вопрос готов" : "Вопрос еще не создан"}</h2>
                </div>

                {question && (
                    <div className="badge-row">
                        <span className="badge">{question.topicName}</span>
                        <span className="badge neutral">{question.difficulty}</span>
                        <span className="badge blue">{question.aiMode}</span>
                    </div>
                )}
            </div>

            <div className={question ? "question-box" : "question-box empty"}>
                {question
                    ? question.questionText
                    : "Выбери пользователя и тему, затем запусти генерацию. Вопрос сохранится как Question в backend."}
            </div>

            <div className="entity-grid">
                <div>
                    <span>Question ID</span>
                    <strong>{question?.questionId || "-"}</strong>
                </div>
                <div>
                    <span>User ID</span>
                    <strong>{question?.userId || "-"}</strong>
                </div>
                <div>
                    <span>Topic ID</span>
                    <strong>{question?.topicId || "-"}</strong>
                </div>
                <div>
                    <span>AI Profile ID</span>
                    <strong>{question?.aiProfileId || "-"}</strong>
                </div>
            </div>
        </section>
    );
}

function AnswerWorkspace({ question, answerText, setAnswerText, onSubmitAnswer, loading }) {
    const remaining = 3000 - answerText.length;

    return (
        <section className="surface answer-surface">
            <div className="surface-header">
                <div>
                    <span className="section-kicker">Answer workspace</span>
                    <h2>Ответ кандидата</h2>
                </div>
                <span className={remaining < 0 ? "counter danger" : "counter"}>
                    {answerText.length}/3000
                </span>
            </div>

            <textarea
                value={answerText}
                onChange={(event) => setAnswerText(event.target.value)}
                placeholder="Напиши ответ так, как будто отвечаешь интервьюеру..."
                disabled={!question}
            />

            <div className="action-bar">
                <button
                    className="primary-button compact"
                    onClick={onSubmitAnswer}
                    disabled={!question || loading || answerText.trim().length === 0 || remaining < 0}
                >
                    {loading ? "Отправляем" : "Отправить ответ"}
                </button>

                <span>
                    {!question
                        ? "Сначала нужен вопрос."
                        : "Ответ сохранится как Answer и уйдет на AI evaluation."}
                </span>
            </div>
        </section>
    );
}

function FeedbackCard({ feedback }) {
    return (
        <section className="surface feedback-surface">
            <div className="surface-header">
                <div>
                    <span className="section-kicker">AI feedback</span>
                    <h2>{feedback ? "Результат проверки" : "Feedback появится после ответа"}</h2>
                </div>
            </div>

            <div className={feedback ? "feedback-body" : "feedback-body empty"}>
                {feedback || "Отправь ответ, и backend вернет обратную связь от выбранного AI-профиля."}
            </div>
        </section>
    );
}

function ActiveProfile({ activeProfile }) {
    return (
        <section className="surface side-surface">
            <div className="surface-header compact-header">
                <div>
                    <span className="section-kicker">AI profile</span>
                    <h2>Активный профиль</h2>
                </div>
            </div>

            {!activeProfile ? (
                <div className="empty-panel">Активный AI-профиль не найден.</div>
            ) : (
                <div className="profile-summary">
                    <div className="profile-title">
                        <span className="status-dot"></span>
                        <div>
                            <strong>{activeProfile.mode}</strong>
                            <p>{activeProfile.descriptionMode || "Описание не указано"}</p>
                        </div>
                    </div>

                    <dl>
                        <div>
                            <dt>Difficulty</dt>
                            <dd>{activeProfile.difficulty || "-"}</dd>
                        </div>
                        <div>
                            <dt>Feedback</dt>
                            <dd>{activeProfile.feedbackMode || "-"}</dd>
                        </div>
                        <div>
                            <dt>Model</dt>
                            <dd>{activeProfile.modelName || "-"}</dd>
                        </div>
                        <div>
                            <dt>Language</dt>
                            <dd>{activeProfile.language || "-"}</dd>
                        </div>
                    </dl>
                </div>
            )}
        </section>
    );
}

function SessionSummary({ selectedUser, selectedTopic, question, answerText, feedback }) {
    return (
        <section className="surface side-surface">
            <div className="surface-header compact-header">
                <div>
                    <span className="section-kicker">Session state</span>
                    <h2>Текущее состояние</h2>
                </div>
            </div>

            <div className="summary-list">
                <div>
                    <span>User</span>
                    <strong>{selectedUser?.username || "-"}</strong>
                </div>
                <div>
                    <span>Topic</span>
                    <strong>{selectedTopic?.name || "-"}</strong>
                </div>
                <div>
                    <span>Question</span>
                    <strong>{question ? `#${question.questionId}` : "-"}</strong>
                </div>
                <div>
                    <span>Answer</span>
                    <strong>{answerText.trim() ? `${answerText.trim().length} chars` : "-"}</strong>
                </div>
                <div>
                    <span>Feedback</span>
                    <strong>{feedback ? "Received" : "-"}</strong>
                </div>
            </div>
        </section>
    );
}

function HistoryList({ history, onLoadHistory, loading }) {
    return (
        <section className="surface history-surface">
            <div className="surface-header">
                <div>
                    <span className="section-kicker">User history</span>
                    <h2>История</h2>
                </div>

                <button className="secondary-button" onClick={onLoadHistory} disabled={loading}>
                    {loading ? "Загрузка" : "Обновить"}
                </button>
            </div>

            {history.length === 0 ? (
                <div className="empty-panel">
                    История не загружена или у пользователя еще нет вопросов.
                </div>
            ) : (
                <div className="history-list">
                    {history.map((item) => (
                        <article className="history-item" key={item.questionId}>
                            <div className="history-heading">
                                <strong>{item.topicName || "Без темы"}</strong>
                                <span>#{item.questionId}</span>
                            </div>
                            <p>{item.textQuestion}</p>
                            <div className="history-answer">
                                {item.answerText || "Ответ еще не сохранен"}
                            </div>
                            <div className="history-meta">
                                <span>{item.username}</span>
                                <span>{item.modelName || "model unknown"}</span>
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </section>
    );
}

function App() {
    const [users, setUsers] = useState([]);
    const [topics, setTopics] = useState([]);
    const [activeProfile, setActiveProfile] = useState(null);
    const [apiStatus, setApiStatus] = useState({ ok: false, label: "Checking" });

    const [userId, setUserId] = useState("");
    const [topicId, setTopicId] = useState("");

    const [question, setQuestion] = useState(null);
    const [answerText, setAnswerText] = useState("");
    const [feedback, setFeedback] = useState("");
    const [history, setHistory] = useState([]);

    const [loadingInitialData, setLoadingInitialData] = useState(false);
    const [loadingQuestion, setLoadingQuestion] = useState(false);
    const [loadingAnswer, setLoadingAnswer] = useState(false);
    const [loadingHistory, setLoadingHistory] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const selectedUser = useMemo(
        () => users.find((user) => String(user.id) === String(userId)),
        [users, userId]
    );

    const selectedTopic = useMemo(
        () => topics.find((topic) => String(topic.id) === String(topicId)),
        [topics, topicId]
    );

    useEffect(() => {
        async function loadInitialData() {
            setLoadingInitialData(true);
            setError("");
            setSuccess("");

            try {
                const healthResponse = await fetch(`${API_URL}/api/health`);
                const healthData = await readResponse(healthResponse);

                if (!healthResponse.ok) {
                    throw new Error("Backend не отвечает.");
                }

                setApiStatus({
                    ok: true,
                    label: typeof healthData === "string" ? "Online" : "Online"
                });

                const usersResponse = await fetch(`${API_URL}/api/users`);
                const topicsResponse = await fetch(`${API_URL}/api/topics`);
                const aiProfilesResponse = await fetch(`${API_URL}/api/aiProfile`);

                const usersData = await readResponse(usersResponse);
                const topicsData = await readResponse(topicsResponse);
                const aiProfilesData = await readResponse(aiProfilesResponse);

                if (!usersResponse.ok) {
                    throw new Error(typeof usersData === "string" ? usersData : "Ошибка при загрузке пользователей.");
                }

                if (!topicsResponse.ok) {
                    throw new Error(typeof topicsData === "string" ? topicsData : "Ошибка при загрузке тем.");
                }

                if (!aiProfilesResponse.ok) {
                    throw new Error(typeof aiProfilesData === "string" ? aiProfilesData : "Ошибка при загрузке AI-профилей.");
                }

                setUsers(usersData);
                setTopics(topicsData);

                const foundActiveProfile = aiProfilesData.find((profile) => profile.active === true);
                setActiveProfile(foundActiveProfile || null);

                if (usersData.length > 0) {
                    setUserId(String(usersData[0].id));
                }

                if (topicsData.length > 0) {
                    setTopicId(String(topicsData[0].id));
                }
            } catch (error) {
                setApiStatus({ ok: false, label: "Offline" });
                setError(error.message || "Не удалось загрузить начальные данные.");
            } finally {
                setLoadingInitialData(false);
            }
        }

        loadInitialData();
    }, []);

    function clearMessages() {
        setError("");
        setSuccess("");
    }

    function validateIds() {
        if (!userId || Number(userId) <= 0) {
            setError("Выбери пользователя.");
            return false;
        }

        if (!topicId || Number(topicId) <= 0) {
            setError("Выбери тему.");
            return false;
        }

        return true;
    }

    async function generateQuestion() {
        clearMessages();

        if (!validateIds()) {
            return;
        }

        setLoadingQuestion(true);

        try {
            const response = await fetch(`${API_URL}/api/interview/question`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: Number(userId),
                    topicId: Number(topicId)
                })
            });

            const data = await readResponse(response);

            if (!response.ok) {
                throw new Error(typeof data === "string" ? data : "Ошибка при генерации вопроса.");
            }

            setQuestion(data);
            setAnswerText("");
            setFeedback("");
            setSuccess("Вопрос успешно сгенерирован.");
        } catch (error) {
            setError(error.message || "Не удалось сгенерировать вопрос.");
        } finally {
            setLoadingQuestion(false);
        }
    }

    async function submitAnswer() {
        clearMessages();

        if (!question) {
            setError("Сначала нужно сгенерировать вопрос.");
            return;
        }

        if (!answerText.trim()) {
            setError("Ответ не может быть пустым.");
            return;
        }

        setLoadingAnswer(true);

        try {
            const response = await fetch(`${API_URL}/api/interview/answer`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: Number(userId),
                    questionId: question.questionId,
                    textAnswer: answerText
                })
            });

            const data = await readResponse(response);

            if (!response.ok) {
                throw new Error(typeof data === "string" ? data : "Ошибка при отправке ответа.");
            }

            setFeedback(data.feedback);
            setSuccess("Ответ отправлен и сохранен.");
        } catch (error) {
            setError(error.message || "Не удалось отправить ответ.");
        } finally {
            setLoadingAnswer(false);
        }
    }

    async function loadHistory() {
        clearMessages();

        if (!userId || Number(userId) <= 0) {
            setError("Выбери пользователя.");
            return;
        }

        setLoadingHistory(true);

        try {
            const response = await fetch(`${API_URL}/api/users/${Number(userId)}/history`);
            const data = await readResponse(response);

            if (!response.ok) {
                throw new Error(typeof data === "string" ? data : "Ошибка при загрузке истории.");
            }

            setHistory(data);
            setSuccess("История обновлена.");
        } catch (error) {
            setError(error.message || "Не удалось загрузить историю.");
        } finally {
            setLoadingHistory(false);
        }
    }

    return (
        <main className="app-shell">
            <AppHeader
                apiStatus={apiStatus}
                usersCount={users.length}
                topicsCount={topics.length}
                activeProfile={activeProfile}
            />

            <WorkflowStepper question={question} feedback={feedback} />

            <ApiNotice type="error" text={error} />
            <ApiNotice type="success" text={success} />

            <div className="workspace-layout">
                <section className="primary-column">
                    <InterviewSettings
                        userId={userId}
                        topicId={topicId}
                        users={users}
                        topics={topics}
                        selectedUser={selectedUser}
                        selectedTopic={selectedTopic}
                        setUserId={setUserId}
                        setTopicId={setTopicId}
                        onGenerateQuestion={generateQuestion}
                        loading={loadingInitialData || loadingQuestion}
                    />

                    <QuestionCard question={question} />

                    <AnswerWorkspace
                        question={question}
                        answerText={answerText}
                        setAnswerText={setAnswerText}
                        onSubmitAnswer={submitAnswer}
                        loading={loadingAnswer}
                    />

                    <FeedbackCard feedback={feedback} />
                </section>

                <aside className="secondary-column">
                    <SessionSummary
                        selectedUser={selectedUser}
                        selectedTopic={selectedTopic}
                        question={question}
                        answerText={answerText}
                        feedback={feedback}
                    />

                    <ActiveProfile activeProfile={activeProfile} />

                    <HistoryList
                        history={history}
                        onLoadHistory={loadHistory}
                        loading={loadingHistory}
                    />
                </aside>
            </div>
        </main>
    );
}

export default App;
