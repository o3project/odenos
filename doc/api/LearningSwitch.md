
## LearningSwitch

**emulates a layer 2 switch.**

----

### REST APIs
  * [GET \<base_uri>/property](#GETproperty)
  * [PUT \<base_uri>/property](#PUTproperty)
  * [GET \<base_uri>/fdb](#GETfdb)
  * [DELETE \<base_uri>/fdb](#DELETEfdb)
  * [DELETE \<base_uri>/fdb/\<mac>](#DELETEfdbmac)
  * [GET \<base_uri>/settings/default_idle_timer](#GETdefault_idle_timer)
  * [PUT \<base_uri>/settings/default_idle_timer](#PUTdefault_idle_timer)
  * [GET \<base_uri>/settings/default_hard_timer](#GETdefault_hard_timer)
  * [PUT \<base_uri>/settings/default_hard_timer](#PUTdefault_hard_timer)

    Here, \<base_uri> is http://\<hostname>:10080/systemmanager/components/learning_switch
    such as http://localhost:10080/systemmanager/components/learning_switch

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
#### <a name="GETfdb">GET <base_uri>/fdb</a>
**get FDB entry.**

##### [Request]:   
  * **Body** :  none

##### [Response]:
  * **Status Code** : 200
  * **Body** :  [FDB](#FDB)   

#### <a name="FDB">[FDB]</a>

key | value      
----|-------
mac | ["node_id" : \<string> , "port_id" : \<string> ]

###### example(JSON)

    {
        "01:02:03:04:05:06": [ 
          "node_id" : "node1",
          "port_id" : "port1"
        ],
        "0A:0B:0C:0D:0E:0F": [
          "node_id" : "node2",
          "port_id" : "port1"
        ],
            …
    }


----
#### <a name="DELETEfdb">DELETE \<base_uri>/fdb</a>  
delete all FDB (Initialize FDB)

##### [Request]:   
  * **Body** :  none

##### [Response]:
  * **Status Code** : 200
  * **Body** :  none

----
#### <a name="DELETEfdbmac">DELETE \<base_uri>/fdb/\<mac></a>  
delete FDB entry.  
mac delimiter(Colon) is not used.

##### [Request]:   
  * **Body** :  none

##### [Response]:
  * **Status Code** : 200
  * **Body** :  none

----
#### <a name="GETdefault_idle_timer">GET <base_uri>/settings/default_idle_timer</a>
get the flow "idle timeout"

##### [Request]:   
  * **Body** :  none

##### [Response]:
  * **Status Code** : 200
  * **Body** :  \<integer>



----
#### <a name="PUTdefault_idle_timer">PUT <base_uri>/settings/default_idle_timer</a>
set the flow "idle timeout"

##### [Request]:   
  * **Body** :  \<integer>

##### [Response]:
  * **Status Code** : 200
  * **Body** :  \<integer>

----
#### <a name="GETdefault_hard_timer">GET <base_uri>/settings/default_hard_timer</a>
get the flow "hard timeout"

##### [Request]:   
  * **Body** :  none

##### [Response]:
  * **Status Code** : 200
  * **Body** :  \<integer>


----
#### <a name="PUTdefault_hard_timer">PUT <base_uri>/settings/default_hard_timer</a>
set the flow "hard timeout"

##### [Request]:   
  * **Body** :  \<integer>

##### [Response]:
  * **Status Code** : 200
  * **Body** :  \<integer>



