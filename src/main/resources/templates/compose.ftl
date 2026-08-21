services:
<#list services as service>
  ${service.name}:
    build: ./${service.name}
    depends_on:
      - db-${service.name}
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${service.name}
    ports:
      - 808${service?counter}:808${service?counter}
  db-${service.name}:
    image: postgres:18
    environment:
      POSTGRES_DB: ${service.name}
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
</#list>