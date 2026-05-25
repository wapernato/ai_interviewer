import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8080";

async function readResponse(response) {
    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }

    return await response.text();
}

function InterviewSettings({
    userId,
    topicId,
    users,
    topics,
    setUserId,
    setTopicId,
    onGenerateQuestion,
    loading
}) {
    return (
        <section className="panel settings-panel">
            <div className="section-header">
                <div>
                    <p className="eyebrow">Step 1</p>
                    <h2>Настройки интервью</h2>
                </div>
                <span className="status-pill">Spring Boot API</span>
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
                                {user.username} — ID {user.id}
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
                                {topic.name} — ID {topic.id}
                            </option>
                        ))}
                    </select>
                </label>
            </div>

            <button
                className="primary-button"
                onClick={onGenerateQuestion}
                disabled={loading || users.length === 0 || topics.length === 0}
            >
                {loading ? "Загружаем..." : "Сгенерировать вопрос"}
            </button>
        </section>
    );
}

function QuestionCard({ question }) {
    if (!question) {
        return (
            <section className="panel empty-state">
                <p className="eyebrow">Step 2</p>
                <h2>Вопрос появится здесь</h2>
                <p>
                    Выбери пользователя и тему, затем нажми кнопку генерации.
                    Backend создаст вопрос и сохранит его в PostgreSQL.
                </p>
            </section>
        );
    }

    return (
        <section className="panel question-panel">
            <div className="section-header">
                <div>
                    <p className="eyebrow">Step 2</p>
                    <h2>Сгенерированный вопрос</h2>
                </div>

                <div className="badge-row">
                    <span className="badge">{question.topicName}</span>
                    <span className="badge badge-dark">{question.difficulty}</span>
                    <span className="badge badge-blue">{question.aiMode}</span>
                </div>
            </div>

            <div className="question-box">
                {question.questionText}
            </div>

            <div className="meta-grid">
                <div>
                    <span>ID вопроса</span>
                    <strong>{question.questionId}</strong>
                </div>

                <div>
                    <span>User ID</span>
                    <strong>{question.userId}</strong>
                </div>

                <div>
                    <span>Topic ID</span>
                    <strong>{question.topicId}</strong>
                </div>

                <div>
                    <span>AI Profile ID</span>
                    <strong>{question.aiProfileId}</strong>
                </div>
            </div>
        </section>
    );
}

function AnswerForm({ question, answerText, setAnswerText, onSubmitAnswer, loading }) {
    return (
        <section className="panel answer-panel">
            <div className="section-header">
                <div>
                    <p className="eyebrow">Step 3</p>
                    <h2>Ответ пользователя</h2>
                </div>

                <span className="counter">{answerText.length} символов</span>
            </div>

            <textarea
                value={answerText}
                onChange={(event) => setAnswerText(event.target.value)}
                placeholder="Напиши ответ так, как будто ты отвечаешь на техническом собеседовании..."
                disabled={!question}
            />

            <div className="actions-row">
                <button
                    className="primary-button"
                    onClick={onSubmitAnswer}
                    disabled={!question || loading}
                >
                    {loading ? "Отправляем ответ..." : "Отправить ответ"}
                </button>

                {!question && (
                    <span className="hint">
                        Сначала нужно сгенерировать вопрос.
                    </span>
                )}
            </div>
        </section>
    );
}

function FeedbackCard({ feedback }) {
    if (!feedback) {
        return null;
    }

    return (
        <section className="panel feedback-panel">
            <p className="eyebrow">Step 4</p>
            <h2>AI Feedback</h2>
            <p>{feedback}</p>
        </section>
    );
}

function AiProfileCard({ activeProfile }) {
    if (!activeProfile) {
        return (
            <section className="panel ai-profile-panel">
                <p className="eyebrow">AI Profile</p>
                <h2>Активный профиль</h2>

                <div className="history-empty">
                    Активный AI-профиль не найден.
                </div>
            </section>
        );
    }

    return (
        <section className="panel ai-profile-panel">
            <p className="eyebrow">AI Profile</p>
            <h2>Активный профиль</h2>

            <div className="profile-card">
                <div className="profile-main">
                    <span className="profile-dot"></span>

                    <div>
                        <h3>{activeProfile.mode}</h3>
                        <p>{activeProfile.descriptionMode || "Описание не указано"}</p>
                    </div>
                </div>

                <div className="profile-grid">
                    <div>
                        <span>Сложность</span>
                        <strong>{activeProfile.difficulty || "—"}</strong>
                    </div>

                    <div>
                        <span>Feedback</span>
                        <strong>{activeProfile.feedbackMode || "—"}</strong>
                    </div>

                    <div>
                        <span>Модель</span>
                        <strong>{activeProfile.modelName || "—"}</strong>
                    </div>

                    <div>
                        <span>Язык</span>
                        <strong>{activeProfile.language || "—"}</strong>
                    </div>
                </div>
            </div>
        </section>
    );
}

function HistoryList({ history, onLoadHistory, loading }) {
    return (
        <section className="panel history-panel">
            <div className="section-header">
                <div>
                    <p className="eyebrow">Step 5</p>
                    <h2>История пользователя</h2>
                </div>

                <button className="secondary-button" onClick={onLoadHistory} disabled={loading}>
                    {loading ? "Загружаем..." : "Показать историю"}
                </button>
            </div>

            {history.length === 0 && (
                <div className="history-empty">
                    История пока не загружена или у пользователя ещё нет сохранённых ответов.
                </div>
            )}

            <div className="history-list">
                {history.map((item) => (
                    <article className="history-item" key={item.questionId}>
                        <div className="history-top">
                            <h3>{item.topicName || "Без темы"}</h3>
                            <span>Question #{item.questionId}</span>
                        </div>

                        <p>
                            <strong>Пользователь:</strong> {item.username}
                        </p>

                        <p>
                            <strong>Вопрос:</strong> {item.textQuestion}
                        </p>

                        <p>
                            <strong>Ответ:</strong> {item.answerText || "Ответ ещё не сохранён"}
                        </p>

                        <p>
                            <strong>Модель:</strong> {item.modelName || "Не указана"}
                        </p>
                    </article>
                ))}
            </div>
        </section>
    );
}

function Message({ type, text }) {
    if (!text) {
        return null;
    }

    return (
        <div className={`message ${type}`}>
            {text}
        </div>
    );
}

function App() {
    const [users, setUsers] = useState([]);
    const [topics, setTopics] = useState([]);
    const [activeProfile, setActiveProfile] = useState(null);

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

    useEffect(() => {
        async function loadInitialData() {
            setLoadingInitialData(true);
            setError("");
            setSuccess("");

            try {
                const usersResponse = await fetch(`${API_URL}/api/users`);
                const topicsResponse = await fetch(`${API_URL}/api/topics`);
                const aiProfilesResponse = await fetch(`${API_URL}/api/aiProfile`);

                const usersData = await readResponse(usersResponse);
                const topicsData = await readResponse(topicsResponse);
                const aiProfilesData = await readResponse(aiProfilesResponse);

                if (!usersResponse.ok) {
                    throw new Error(
                        typeof usersData === "string"
                            ? usersData
                            : "Ошибка при загрузке пользователей."
                    );
                }

                if (!topicsResponse.ok) {
                    throw new Error(
                        typeof topicsData === "string"
                            ? topicsData
                            : "Ошибка при загрузке тем."
                    );
                }

                if (!aiProfilesResponse.ok) {
                    throw new Error(
                        typeof aiProfilesData === "string"
                            ? aiProfilesData
                            : "Ошибка при загрузке AI-профилей."
                    );
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
                throw new Error(
                    typeof data === "string"
                        ? data
                        : "Ошибка при генерации вопроса."
                );
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
                throw new Error(
                    typeof data === "string"
                        ? data
                        : "Ошибка при отправке ответа."
                );
            }

            setFeedback(data.feedback);
            setSuccess("Ответ успешно отправлен и сохранён.");
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
                throw new Error(
                    typeof data === "string"
                        ? data
                        : "Ошибка при загрузке истории."
                );
            }

            setHistory(data);
            setSuccess("История успешно загружена.");
        } catch (error) {
            setError(error.message || "Не удалось загрузить историю.");
        } finally {
            setLoadingHistory(false);
        }
    }

    return (
        <div className="page">
            <div className="background-glow background-glow-one"></div>
            <div className="background-glow background-glow-two"></div>

            <main className="app">
                <header className="hero">
                    <div className="hero-badge">
                        Java Backend · Spring Boot · PostgreSQL · React
                    </div>

                    <h1>AI Interviewer</h1>

                    <p>
                        Персональный тренажёр технических собеседований.
                        Выбери пользователя и тему, получи вопрос, отправь ответ
                        и посмотри feedback с историей.
                    </p>
                </header>

                <Message type="error" text={error} />
                <Message type="success" text={success} />

                <div className="layout">
                    <div className="main-column">
                        <InterviewSettings
                            userId={userId}
                            topicId={topicId}
                            users={users}
                            topics={topics}
                            setUserId={setUserId}
                            setTopicId={setTopicId}
                            onGenerateQuestion={generateQuestion}
                            loading={loadingInitialData || loadingQuestion}
                        />

                        <QuestionCard question={question} />

                        <AnswerForm
                            question={question}
                            answerText={answerText}
                            setAnswerText={setAnswerText}
                            onSubmitAnswer={submitAnswer}
                            loading={loadingAnswer}
                        />

                        <FeedbackCard feedback={feedback} />
                    </div>

                    <aside className="side-column">
                        <section className="panel summary-panel">
                            <p className="eyebrow">Current session</p>
                            <h2>Состояние</h2>

                            <div className="summary-row">
                                <span>User ID</span>
                                <strong>{userId || "—"}</strong>
                            </div>

                            <div className="summary-row">
                                <span>Topic ID</span>
                                <strong>{topicId || "—"}</strong>
                            </div>

                            <div className="summary-row">
                                <span>Question</span>
                                <strong>{question ? `#${question.questionId}` : "—"}</strong>
                            </div>

                            <div className="summary-row">
                                <span>Answer length</span>
                                <strong>{answerText.length}</strong>
                            </div>
                        </section>

                        <AiProfileCard activeProfile={activeProfile} />

                        <HistoryList
                            history={history}
                            onLoadHistory={loadHistory}
                            loading={loadingHistory}
                        />
                    </aside>
                </div>
            </main>
        </div>
    );
}

export default App;