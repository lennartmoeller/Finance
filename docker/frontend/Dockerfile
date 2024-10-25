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

ARG NGINX_BACKEND_API_REDIRECT=false

RUN echo ' \
server { \
    listen       80; \
    listen  [::]:80; \
    server_name  localhost; \
    \
    ' > /etc/nginx/conf.d/default.conf && \
    if [ "$NGINX_BACKEND_API_REDIRECT" = "true" ]; then \
        echo "    location /api/ { \
            proxy_pass http://backend:8080; \
            proxy_set_header Host \$host; \
            proxy_set_header X-Real-IP \$remote_addr; \
            proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for; \
            proxy_set_header X-Forwarded-Proto \$scheme; \
        }" >> /etc/nginx/conf.d/default.conf; \
    fi && \
    echo " \
    location / { \
        root   /usr/share/nginx/html; \
        index  index.html; \
        try_files \$uri /index.html; \
    } \
} \
" >> /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
