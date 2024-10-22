# First build the application.

FROM node:18 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY src/main/tsx src/main/tsx
COPY src/main/resources/frontend src/main/resources/frontend
COPY tsconfig.json .
COPY webpack.config.js .

RUN npm run build

# Now serve the files using Nginx.

FROM nginx:1.27.2

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
