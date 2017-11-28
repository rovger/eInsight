{
	"$match": {
		"${matchCondition}": {
			"$gte": {"$date":"${startTime}"},
			"$lt": {"$date":"${endTime}"}
		}
	}
}