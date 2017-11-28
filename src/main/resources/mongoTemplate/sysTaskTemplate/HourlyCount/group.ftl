{
	"$group": {
		"_id": {
			"countby": "$ColumnValue"
		},
		"count": {
			"$sum": "$Count"
		}
	}
}