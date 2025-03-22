/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.neuq.techhub;

import edu.neuq.techhub.utils.IpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleCommentTest {

    @Test
    public void testIp2Region() {
        String region = IpUtils.getIp2region("121.22.88.5");
        System.out.println(region);
        region = IpUtils.getIp2region("127.0.0.1");
        System.out.println(region);
    }

    @Test
    public void testIpUtils() {
        System.out.println(IpUtils.getHostIp());
        System.out.println(IpUtils.getHostName());
    }
}
