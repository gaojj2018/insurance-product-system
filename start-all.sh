#!/bin/bash

# 保险核心系统 - 启动所有服务
# 必须在项目根目录下运行

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

# 创建日志目录
mkdir -p logs

# 设置Java_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

echo "========================================"
echo "保险核心系统 - 启动所有服务"
echo "========================================"

# 1. 启动Eureka注册中心
echo "[1/14] 启动Eureka注册中心..."
cd eureka-server
java -jar target/eureka-server-1.0.0.jar > logs/eureka.log 2>&1 &
EUREKA_PID=$!
echo "Eureka已启动, PID: $EUREKA_PID"
cd ..

sleep 15

# 2. 启动Config Server
echo "[2/14] 启动Config Server..."
cd config-service
java -jar target/config-service-1.0.0.jar > logs/config.log 2>&1 &
CONFIG_PID=$!
echo "Config Server已启动, PID: $CONFIG_PID"
cd ..

sleep 10

# 3. 启动Spring Boot Admin
echo "[3/14] 启动Spring Boot Admin..."
cd admin-server
java -jar target/admin-server-1.0.0.jar > logs/admin.log 2>&1 &
ADMIN_PID=$!
echo "Admin已启动, PID: $ADMIN_PID"
cd ..

sleep 10

# 4. 启动Auth Service (如果未编译则先编译)
echo "[4/14] 启动Auth Service..."
if [ ! -f "auth-service/target/auth-service-1.0.0.jar" ]; then
    echo "    正在编译auth-service..."
    cd auth-service
    mvn clean package -DskipTests
    cd ..
fi
cd auth-service
java -jar target/auth-service-1.0.0.jar > logs/auth.log 2>&1 &
AUTH_PID=$!
echo "Auth Service已启动, PID: $AUTH_PID"
cd ..

sleep 10

# 5. 启动Gateway
echo "[5/14] 启动Gateway..."
cd gateway
java -jar target/gateway-1.0.0.jar > logs/gateway.log 2>&1 &
GATEWAY_PID=$!
echo "Gateway已启动, PID: $GATEWAY_PID"
cd ..

sleep 10

# 6. 启动产品服务
echo "[6/14] 启动产品服务..."
cd backend
java -jar target/product-service-1.0.0.jar > logs/product.log 2>&1 &
PRODUCT_PID=$!
echo "产品服务已启动, PID: $PRODUCT_PID"
cd ..

# 7. 启动客户管理服务
echo "[7/14] 启动客户管理服务..."
cd customer-service
java -jar target/customer-service-1.0.0.jar > logs/customer.log 2>&1 &
CUSTOMER_PID=$!
echo "客户管理服务已启动, PID: $CUSTOMER_PID"
cd ..

# 8. 启动投保申请服务
echo "[8/14] 启动投保申请服务..."
cd application-service
java -jar target/application-service-1.0.0.jar > logs/application.log 2>&1 &
APPLICATION_PID=$!
echo "投保申请服务已启动, PID: $APPLICATION_PID"
cd ..

# 9. 启动核保服务
echo "[9/14] 启动核保服务..."
cd underwriting-service
java -jar target/underwriting-service-1.0.0.jar > logs/underwriting.log 2>&1 &
UNDERWRITING_PID=$!
echo "核保服务已启动, PID: $UNDERWRITING_PID"
cd ..

# 10. 启动保单管理服务
echo "[10/14] 启动保单管理服务..."
cd policy-service
java -jar target/policy-service-1.0.0.jar > logs/policy.log 2>&1 &
POLICY_PID=$!
echo "保单管理服务已启动, PID: $POLICY_PID"
cd ..

# 11. 启动理赔服务
echo "[11/14] 启动理赔服务..."
cd claims-service
java -jar target/claims-service-1.0.0.jar > logs/claims.log 2>&1 &
CLAIMS_PID=$!
echo "理赔服务已启动, PID: $CLAIMS_PID"
cd ..

# 12. 启动财务服务
echo "[12/14] 启动财务服务..."
cd finance-service
java -jar target/finance-service-1.0.0.jar > logs/finance.log 2>&1 &
FINANCE_PID=$!
echo "财务服务已启动, PID: $FINANCE_PID"
cd ..

# 13. 启动消息服务
echo "[13/14] 启动消息服务..."
cd message-service
java -jar target/message-service-1.0.0.jar > logs/message.log 2>&1 &
MESSAGE_PID=$!
echo "消息服务已启动, PID: $MESSAGE_PID"
cd ..

# 14. 启动报表服务
echo "[14/14] 启动报表服务..."
cd report-service
java -jar target/report-service-1.0.0.jar > logs/report.log 2>&1 &
REPORT_PID=$!
echo "报表服务已启动, PID: $REPORT_PID"
cd ..

echo ""
echo "========================================"
echo "所有服务已启动完成！"
echo "========================================"
echo ""
echo "访问地址:"
echo "  前端(Nginx): http://localhost"
echo "  Gateway:    http://localhost:8888"
echo "  Eureka:     http://localhost:8761"
echo "  Admin:      http://localhost:8090"
echo "  Config:     http://localhost:8089"
echo ""
echo "服务端口:"
echo "  Product:     8080  (产品服务)"
echo "  Customer:    8082  (客户管理)"
echo "  Application: 8083  (投保申请)"
echo "  Underwriting: 8084 (核保)"
echo "  Policy:      8085  (保单管理)"
echo "  Claims:      8086  (理赔)"
echo "  Finance:     8087  (财务)"
echo "  Message:     8088  (消息)"
echo "  Report:      8091  (报表)"
echo "  Auth:        8092  (认证)"
echo ""
echo "登录账号: admin / 123456"
echo "========================================"
