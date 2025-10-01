# -------------------------------
# Stage 1: Build Angular inside Node
# -------------------------------
    FROM node:20 AS build
    WORKDIR /app
    
    # Copy package files and install dependencies
    COPY package*.json ./
    RUN npm install
    
    # Copy the rest of the Angular source code
    COPY . .
    
    # Build Angular for production
    RUN npm run build -- --configuration production
    
    # -------------------------------
    # Stage 2: Serve Angular with Nginx + SSL
    # -------------------------------
    FROM nginx:latest
    
    # Install OpenSSL (needed to generate certs)
    RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*
    
    # Remove default Nginx HTML
    RUN rm -rf /usr/share/nginx/html/*
    
    # Copy Angular production build from previous stage
    COPY --from=build /app/dist/nvtfrontend/browser /usr/share/nginx/html
    
    # Copy your custom Nginx configuration
    COPY nginx.conf /etc/nginx/nginx.conf
    
    # Generate self-signed SSL certs (valid 365 days)
    RUN mkdir -p /etc/ssl/private /etc/ssl/certs && \
        openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
        -keyout /etc/ssl/private/nginx-selfsigned.key \
        -out /etc/ssl/certs/nginx-selfsigned.crt \
        -subj "/CN=localhost"
    
    # Expose ports
    EXPOSE 80 443
    
    # Start Nginx in foreground
    CMD ["nginx", "-g", "daemon off;"]
    