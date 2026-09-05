# codegen

A cli application
that generates all the needed files
to start Spring Boot microservice project development

It does that so by taking a YAML file
that describes what to generate

## Usage

```bash
$ codegen <FILE> [OPTIONS]

codegen - a Java & Spring Boot code generator

ARGUMENTS:
    <FILE>                  path of YAML spec file

OPTIONS:
    -o, --output <DIR>      specify custom generation directory [default: ~/IdeaProjects]
```

#### YAML spec file example
```yaml
project:
  name: marketplace
  basePackage: com.github.iknonuffin.marketplace

generator:
  generateControllers: false
  generateServices: false
  generateRepositories: false
  generateDtos: false
  generateEvents: false

services:

  - name: order-service

    packageName: order

    enums:
      - name: OrderStatus
        values:
          - CREATED
          - CONFIRMED
          - CANCELLED
          - COMPLETED

    entities:
      - name: Order

        fields:
          id: Long
          userId: Long
          status: OrderStatus
          total: BigDecimal

  - name: product-service

    packageName: product

    entities:
      - name: Product

        fields:
          id: Long
          name: String
          price: BigDecimal

  - name: user-service

    packageName: user

    entities:
      - name: User

        fields:
          id: Long
          username: String
          password: String
```