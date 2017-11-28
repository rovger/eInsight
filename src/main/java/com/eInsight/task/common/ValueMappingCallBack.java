package com.eInsight.task.common;

import java.util.List;

public interface ValueMappingCallBack
{
  String callback(List<String> paramList);

  List<String> reverseCallback(String paramString);
}
