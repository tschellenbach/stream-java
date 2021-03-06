/**

 Copyright (c) 2015, Alessandro Pieri
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies,
 either expressed or implied, of the FreeBSD Project.

 */
package io.getstream.client.service;

import io.getstream.client.exception.StreamClientException;
import io.getstream.client.model.activities.BaseActivity;
import io.getstream.client.model.activities.NotificationActivity;
import io.getstream.client.model.beans.MarkedActivity;
import io.getstream.client.model.beans.StreamResponse;
import io.getstream.client.model.filters.FeedFilter;

import java.io.IOException;

/**
 * Provide methods to interact with Notification activities of subtype of {@link BaseActivity}.
 *
 * @param <T>
 */
public interface NotificationActivityService<T extends BaseActivity> {

    /**
     * List notification activities using the given filter.
     * Futhermore, mark a list of given activities {@link MarkedActivity} as read and/or seen.
     *
     * @param filter
     * @param markAsRead A list of activity ids to be marked as read.
     * @param markAsSeen A list of activity ids to be marked as seen.
     * @return
     * @throws IOException
     * @throws StreamClientException
     */
    StreamResponse<NotificationActivity<T>> getActivities(FeedFilter filter, MarkedActivity markAsRead, MarkedActivity markAsSeen) throws IOException, StreamClientException;

    /**
     * List notification activities using the given filter.
     * Futhermore, mark all activities {@link MarkedActivity} as read and/or seen.
     *
     * @param filter
     * @param markAsRead If true, mark all the activities as read. If false leave them untouched.
     * @param markAsSeen If true, mark all the activities as seen. If false leave them untouched.
     * @return
     * @throws IOException
     * @throws StreamClientException
     */
    StreamResponse<NotificationActivity<T>> getActivities(FeedFilter filter, boolean markAsRead, boolean markAsSeen) throws IOException, StreamClientException;

    /**
     * Get a list of activities using the standard filter (limit = 25).
     *
     * @return
     * @throws IOException
     * @throws StreamClientException
     */
    StreamResponse<NotificationActivity<T>> getActivities() throws IOException, StreamClientException;
}
