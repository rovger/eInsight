{
	"$match": {
		"StartDate": {
			"$gte": {"$date":"${startTime}"},
			"$lt": {"$date":"${endTime}"}
		},
		"ColumnName":"${taskName}",
		"ColumnValue":{"$exists":true}
	}
}