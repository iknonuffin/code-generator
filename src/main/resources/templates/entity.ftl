package ${basePackage}.entity;
<#if imports.other?has_content>

  <#list imports.other as importName>
import ${importName};
  </#list>
</#if>
<#if imports.java?has_content>

  <#list imports.java as importName>
import ${importName};
  </#list>
</#if>

@Entity
@NoArgsConstructor
@Getter @Setter
public class ${entity.name} {

<#list entity.fields as fieldName, fieldType>
  <#if fieldName == "id">
    @Id
    <#if fieldType == "Long">
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    <#elseif fieldType == "UUID">
    @GeneratedValue(strategy = GenerationType.UUID)
    </#if>
  </#if>
    private ${fieldType} ${fieldName};

</#list>
}