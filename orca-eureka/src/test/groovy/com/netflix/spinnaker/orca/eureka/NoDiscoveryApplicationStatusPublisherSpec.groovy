/*
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.netflix.spinnaker.orca.eureka

import com.netflix.spinnaker.kork.eureka.RemoteStatusChangedEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification
import spock.lang.Subject
import static com.netflix.appinfo.InstanceInfo.InstanceStatus.UP

class NoDiscoveryApplicationStatusPublisherSpec extends Specification {

  ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)
  @Subject publisher = new NoDiscoveryApplicationStatusPublisher(eventPublisher)

  def "emits an UP event immediately on context ready"() {
    when:
    publisher.onApplicationEvent(new ContextRefreshedEvent(Stub(ApplicationContext)))

    then:
    1 * eventPublisher.publishEvent({ RemoteStatusChangedEvent e -> e.source.current == UP })
  }

}
