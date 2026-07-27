# AI Interviewer Frontend

React + TypeScript frontend for the AI Interviewer backend.

## Stack

- React
- TypeScript
- Vite
- React Router
- TanStack Query
- Axios
- React Hook Form
- Zod
- Vitest + Testing Library

## Environment

Create `.env` from `.env.example`:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## Commands

```bash
npm install
npm run dev
npm run build
npm run lint
npm test
```

The backend should be available at `VITE_API_BASE_URL`.

## Implemented Pages

- `/login` - login
- `/register` - registration
- `/` - user dashboard
- `/topics` - public topics list
- `/interview` - interview flow
- `/ai-profiles` - public AI profiles list
- `/history` - current user's interview history
- `/admin/topics` - admin topic management
- `/admin/ai-profiles` - admin AI profile management
- `*` - 404

## API Notes

The frontend uses a single Axios client from `src/api/client.ts`.
The client reads `VITE_API_BASE_URL`, attaches a Bearer token from local storage, and converts backend error responses into readable messages.

Backend remains the source of truth for authorization. UI routes and buttons are hidden by role only for user experience; protected backend endpoints must still reject invalid access.
