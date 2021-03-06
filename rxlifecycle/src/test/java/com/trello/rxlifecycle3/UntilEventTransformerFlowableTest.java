/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle3;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.TestSubscriber;

public class UntilEventTransformerFlowableTest {

    PublishProcessor<String> stream;
    PublishSubject<String> lifecycle;

    @Before
    public void setup() {
        stream = PublishProcessor.create();
        lifecycle = PublishSubject.create();
    }

    @Test
    public void noEvents() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        stream.onNext("2");
        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void oneWrongEvent() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertNotTerminated();
    }

    @Test
    public void twoEvents() {
        TestSubscriber<String> testSubscriber = stream
            .compose(RxLifecycle.<String, String>bindUntilEvent(lifecycle, "stop"))
            .test();

        stream.onNext("1");
        lifecycle.onNext("keep going");
        stream.onNext("2");
        lifecycle.onNext("stop");
        stream.onNext("3");

        testSubscriber.assertValues("1", "2");
        testSubscriber.assertComplete();
    }

}