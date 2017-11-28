{
	"$group": {
		"_id": {
		    <#list pathList as path>
			   "countby${path_index+1}": "$${path}"<#sep>, </#sep>
			</#list>
		},
		"count": {
			"$sum": 1
		}
	}
}