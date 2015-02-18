
## ComponentManager

----

### REST APIs
  * [GET \<base_uri>/property](#GETproperty)
  * [PUT \<base_uri>/property](#PUTproperty)
  * [GET \<base_uri>/component_types](#GETcomponent_types)
  * [GET \<base_uri>/components](#GETcomponents)

----
#### <a name="GETproperty"> GET \<base_uri>/property</a>
  * **get Object property.**

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)
 
----
#### <a name="PUTproperty"> PUT \<base_uri>/property</a>
  * **update Object property.**

##### [Request]:   
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)

##### [Response]:
  * **Status Code** : 200
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)

----
#### <a name="GETcomponent_types"> GET \<base_uri>/component_types</a>
get ComponentType list.    
ComponentType is Registered Component.Property.type

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** : dict {ComponentType:{"type" :\<string>   "super_type":\<string> , "connection_types":{\<string>: \<string>} , "description":\<string> }

##### example(JSON)
<pre>
 {
   "Aggregator"  : { "type": "Aggregator" ,  "super_type":"Aggregator" ,
                     "connection_types":{"original":"1", "aggregated":"1"}, "description":"Aggregator ...." },
   "Slicer"  : { "type": "Slicer" ,  "super_type":"Slicer" ,
                 "connection_types":{"original":"1", "Sliver":"*"}, "description":"Slicer  ...." },
   "Federator"  : { "type": "Federator" ,  "super_type":"Federator" ,
                    "connection_types":{"original":"*", "Federeted":"1"}, "description":"Federator  ...." }
   }
</pre>

**Key**          | **Value** |**Description**                            
-----------------|-----------|------------------------------------------
type             | \<String> |ObjectType (example:ComponentManager,Network,Slicer,,,)
super_type       | \<String> |super class ObjectType. (example:ComponentManager,NetworkComponent,Slicer,,,)
connection_types | dict{\<String>:\<String>} | [ComponentConnection](#ComponentConnection).connection_type : connection max num
description      | \<String> |




----
#### <a name="GETcomponents"> GET \<base_uri>/components</a>
**get Component instance List.**

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** : dict { [Component.Property](./DataClass.md#ObjectProperty).id, [Component.Property](./DataClass.md#ObjectProperty) }
  * **Note** : Component.Property is Component instance information.
