{
	"$project":{
	    "_id":0, 
	    <#list pathList as path>
		   "${path}" : 1 <#sep>, </#sep>
	    </#list>
	 }
}