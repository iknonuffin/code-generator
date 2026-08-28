package ${basePackage}.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
<#-- @Table(name = "${}") -->
@NoArgsConstructor
@Getter @Setter
public class ${entity.name} {

<#list entity.fields as fieldName, fieldType>
  <#-- @Column(nullable = ${}, unique = ${}) -->
  <#if fieldName == "id">
    @Id
    <#if fieldType = "Long">
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    <#elseif fieldType == "UUID">
    @GeneratedValue(strategy = GenerationType.UUID)
    </#if>
  </#if>
    private ${fieldType} ${fieldName};

</#list>
}