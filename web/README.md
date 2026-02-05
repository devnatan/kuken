# web-ui

## Building for Local Development

**Clone the project repository**

```
git clone https://github.com/devnatan/kuken.git
```

**Run for local development**

TypeScript, Node and NPM are needed to run this project.

```
npm run dev
```

Also is needed to configure environment variables, create a `.env.local` file
containing the environment variables needed to run the project locally.

**Format and lint codebase**

We use ESLint as linter and Prettier as code formatter.

```
npm run format
npm run lint
```

## Building for Production

```
npm run build
```
