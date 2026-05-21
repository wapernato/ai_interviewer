import { useState } from "react";
import "./App.css";

function QuestionCard({ question }) {
    return (
        <div className="card">
            <h2>Текущий вопрос</h2>

            <p>
                <strong>ID вопроса:</strong> {question.questionId}
            </p>

            <p>
                <strong>Тема:</strong> {question.topicName}
            </p>

            <p>
                <strong>AI-режим:</strong> {question.aiMode}
            </p>

            <p>
                <strong>Сложность:</strong> {question.difficulty}
            </p>

            <p>
                <strong>Вопрос:</strong> {question.questionText}
            </p>

            <p>
                <strong>Язык:</strong> {question.language}
            </p>
        </div>
    );
}

function InterviewSettings({ userId, topicId, setUserId, setTopicId }) {
    return (
        <div className="card">
            <h2>Настройки интервью</h2>

            <label>
                User ID
                <input
                    value={userId}
                    onChange={(event) => setUserId(event.target.value)}
                    placeholder="Введите id пользователя"
                />
            </label>

            <label>
                Topic ID
                <input
                    value={topicId}
                    onChange={(event) => setTopicId(event.target.value)}
                    placeholder="Введите id темы"
                />
            </label>

            <p>
                Сейчас выбран пользователь с id: <strong>{userId}</strong>
            </p>

            <p>
                Сейчас выбрана тема с id: <strong>{topicId}</strong>
            </p>
        </div>
    );
}

function App() {
    const [userId, setUserId] = useState("1");
    const [topicId, setTopicId] = useState("1");

    const [question, setQuestion] = useState({
        questionId: 5,
        userId: 1,
        topicId: 1,
        aiProfileId: 3,
        topicName: "Java Core",
        questionText: "Объясните разницу между ArrayList и LinkedList. В каких случаях лучше использовать каждый из них?",
        aiMode: "Zloi",
        difficulty: "middle",
        language: "ru"
    });

    function changeQuestion() {
        setQuestion({
            ...question,
            questionId: question.questionId + 1,
            userId: Number(userId),
            topicId: Number(topicId),
            topicName: "Тема с id " + topicId,
            questionText: "Тестовый вопрос для пользователя " + userId + " по теме " + topicId
        });
    }

    return (
        <div className="app">
            <h1>AI Interviewer</h1>

            <InterviewSettings
                userId={userId}
                topicId={topicId}
                setUserId={setUserId}
                setTopicId={setTopicId}
            />

            <QuestionCard question={question} />

            <div className="card">
                <button onClick={changeQuestion}>
                    Сгенерировать тестовый вопрос
                </button>
            </div>
        </div>
    );
}

export default App;