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
    # Stage 2: Serve Angular with Nginx
    # -------------------------------
    FROM nginx:latest
    # Remove default Nginx HTML
    RUN rm -rf /usr/share/nginx/html/*
    
    # Copy Angular production build from previous stage
    COPY --from=build /app/dist/nvtfrontend/browser /usr/share/nginx/html
    
    # Copy your custom Nginx configuration
    COPY nginx.conf /etc/nginx/nginx.conf
    
    # Expose ports
    EXPOSE 80 443
    
    # Start Nginx in foreground
    CMD ["nginx", "-g", "daemon off;"]
    