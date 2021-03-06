/*
 * Copyright (c) 2019 Schibsted Media Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.clouddriver.tasks.providers.aws.cloudformation;

import com.netflix.spinnaker.orca.Task;
import com.netflix.spinnaker.orca.TaskResult;
import com.netflix.spinnaker.orca.clouddriver.CloudDriverCacheService;
import com.netflix.spinnaker.orca.clouddriver.tasks.AbstractCloudProviderAwareTask;
import com.netflix.spinnaker.orca.pipeline.model.Stage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit.RetrofitError;

@Component
public class CloudFormationForceCacheRefreshTask extends AbstractCloudProviderAwareTask
    implements Task {
  static final String REFRESH_TYPE = "CloudFormation";

  @Autowired CloudDriverCacheService cacheService;

  @Override
  public TaskResult execute(@Nonnull Stage stage) {
    String cloudProvider = getCloudProvider(stage);

    Map<String, Object> data = new HashMap<>();

    String credentials = getCredentials(stage);
    if (credentials != null) {
      data.put("credentials", credentials);
    }

    List<String> regions = (List<String>) stage.getContext().get("regions");
    if (regions != null && !regions.isEmpty()) {
      data.put("region", regions);
    }

    String stackName = (String) stage.getContext().get("stackName");
    if (stackName != null) {
      data.put("stackName", stackName);
    }

    try {
      cacheService.forceCacheUpdate(cloudProvider, REFRESH_TYPE, data);
    } catch (RetrofitError e) {
      return TaskResult.RUNNING;
    }
    return TaskResult.SUCCEEDED;
  }
}
