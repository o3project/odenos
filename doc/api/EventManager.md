
## EvenManager

**EventManager is a class of implementing pub-sub communication between RemoteObject instances**    
**Followings are brief description of EventManager's REST API**

----

### REST APIs
  * [GET \<base_uri>/property](#GETproperty)
  * [PUT \<base_uri>/property](#PUTproperty)
  * [GET \<base_uri>/settings/event_subscriptions](#GETevent_subscriptions)
  * [PUT \<base_uri>/settings/event_subscriptions/\<subscriber_id>](#PUTevent_subscriptions_id)
  * [GET \<base_uri>/settings/event_subscriptions/\<subscriber_id>](#GETevent_subscriptions_id)

----
#### <a name="GETproperty"> GET \<base_uri>/property</a>  
get Object property.  

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)
 
----
#### <a name="PUTproperty"> PUT \<base_uri>/property</a>  
update Object property.

##### [Request]:   
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)

##### [Response]:
  * **Status Code** : 200
  * **Body** :  [ObjectProperty](./DataClass.md#ObjectProperty)

----
#### <a name="GETevent_subscriptions">GET \<base_uri>/settings/event_subscriptions</a> 
get Event Subscription list.    
\<subscriber_id> is EventSubscription.subscriber_id

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** : dict{subscriber_id, [EventSubscription](./DataClass.md#EventSubscription)} 

----
#### <a name="PUTevent_subscriptions_id">PUT \<base_uri>/settings/event_subscriptions/\<subscriber_id></a>
Event Subscription settings make a new registration.

##### [Request]:   
  * **Body** : [EventSubscription](./DataClass.md#EventSubscription)

##### [Response]:
  * **Status Code** : 200
  * **Body** : [EventSubscription](./DataClass.md#EventSubscription)

----
#### <a name="GETevent_subscriptions_id">GET \<base_uri>/settings/event_subscriptions/\<subscriber_id></a>  
get Event Subscription. <subscriber_id> is EventSubscription.subscriber_id  

##### [Request]:   
  * **Body** : none 

##### [Response]:
  * **Status Code** : 200
  * **Body** : [EventSubscription](./DataClass.md#EventSubscription)

