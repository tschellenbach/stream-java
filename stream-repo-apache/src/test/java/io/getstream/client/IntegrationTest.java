package io.getstream.client;

import io.getstream.client.config.ClientConfiguration;
import io.getstream.client.exception.AuthenticationFailedException;
import io.getstream.client.exception.InvalidOrMissingInputException;
import io.getstream.client.exception.StreamClientException;
import io.getstream.client.model.activities.NotificationActivity;
import io.getstream.client.model.activities.SimpleActivity;
import io.getstream.client.model.beans.FeedFollow;
import io.getstream.client.model.beans.StreamResponse;
import io.getstream.client.model.feeds.Feed;
import io.getstream.client.model.filters.FeedFilter;
import io.getstream.client.service.AggregatedActivityServiceImpl;
import io.getstream.client.service.FlatActivityServiceImpl;
import io.getstream.client.service.NotificationActivityServiceImpl;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntegrationTest {

    public static final String API_KEY = "nfq26m3qgfyp";
    public static final String API_SECRET = "245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2";

    @BeforeClass
    public static void setLog() {
//        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
    }

    public String getTestUserId(String userId) {
        long millis = System.currentTimeMillis();
        return String.format("%s_%d", userId, millis);
    }

    @Test
    public void shouldGetFollowers() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("2");
        Feed feed = streamClient.newFeed("user", userId);
        streamClient.shutdown();
    }

    @Ignore
    public void shouldGetFollowing() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("user", "2");
        List<FeedFollow> following = feed.getFollowing();
        assertThat(following.size(), is(3));
        streamClient.shutdown();
    }

    @Test
    public void shouldFollow() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        String followerId = this.getTestUserId("follower");
        Feed feed = streamClient.newFeed("user", followerId);

        List<FeedFollow> following = feed.getFollowing();
        assertThat(following.size(), is(0));

        feed.follow("user", "1");
        feed.follow("user", "2");
        feed.follow("user", "3");

        List<FeedFollow> followingAfter = feed.getFollowing();
        assertThat(followingAfter.size(), is(3));

        streamClient.shutdown();
    }

    @Test
    public void shouldUnfollow() throws IOException, StreamClientException, InterruptedException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        String followerId = this.getTestUserId("follower");
        Feed feed = streamClient.newFeed("user", followerId);

        List<FeedFollow> following = feed.getFollowing();
        assertThat(following.size(), is(0));

        feed.follow("user", "1");
        feed.follow("user", "2");
        feed.follow("user", "3");

        List<FeedFollow> followingAfter = feed.getFollowing();
        assertThat(followingAfter.size(), is(3));
        feed.unfollow("user", "2");

        List<FeedFollow> followingAgain = feed.getFollowing();
        assertThat(followingAgain.size(), is(2));
        streamClient.shutdown();
    }

    @Test
    public void shouldGetActivities() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        String userId = this.getTestUserId("shouldGetActivities");
        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        for (SimpleActivity activity : flatActivityService.getActivities().getResults()) {
            MatcherAssert.assertThat(activity.getId(), containsString("11e4-8080"));
        }
        streamClient.shutdown();
    }

    @Test
    public void shouldAddActivity() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("user", "2");
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setVerb("verb");
        flatActivityService.addActivity(activity);
        streamClient.shutdown();
    }

    @Test
    public void shouldAddActivityToRecipients() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("user", "2");
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setTo(Arrays.asList("user:1", "user:4"));
        activity.setVerb("verb");
        flatActivityService.addActivity(activity);
        streamClient.shutdown();
    }

    @Test
    public void shouldAddAndRetrieveActivity() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldAddAndRetrieveActivityToRecipients");

        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setVerb("verb");
        List<SimpleActivity> firstRequest = flatActivityService.getActivities().getResults();
        assertThat(firstRequest.size(), is(0));
        flatActivityService.addActivity(activity);
        List<SimpleActivity> secondRequest = flatActivityService.getActivities().getResults();
        assertThat(secondRequest.size(), is(1));
        streamClient.shutdown();
    }

    @Test
    public void shouldAddAndRetrieveActivityToRecipients() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldAddAndRetrieveActivityToRecipients");
        String recipientId1 = this.getTestUserId("shouldAddAndRetrieveActivityToRecipients1");
        String recipientId2 = this.getTestUserId("shouldAddAndRetrieveActivityToRecipients2");

        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setTo(Arrays.asList(String.format("user:%s", recipientId1), String.format("user:%s", recipientId2)));
        activity.setVerb("verb");
        List<SimpleActivity> firstRequest = flatActivityService.getActivities().getResults();
        assertThat(firstRequest.size(), is(0));
        flatActivityService.addActivity(activity);
        List<SimpleActivity> secondRequest = flatActivityService.getActivities().getResults();
        assertThat(secondRequest.size(), is(1));

        // retrieve the list of activities from the other 2 feeds too
        Feed feedRecipient1 = streamClient.newFeed("user", recipientId1);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService1 = feedRecipient1.newFlatActivityService(SimpleActivity.class);
        List<SimpleActivity> thirdRequest = flatActivityService1.getActivities().getResults();
        assertThat(thirdRequest.size(), is(1));

        Feed feedRecipient2 = streamClient.newFeed("user", recipientId2);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService2 = feedRecipient2.newFlatActivityService(SimpleActivity.class);
        List<SimpleActivity> forthRequest = flatActivityService2.getActivities().getResults();
        assertThat(forthRequest.size(), is(1));

        streamClient.shutdown();
    }

    @Test
    public void shouldReturnActivityId() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldReturnActivityId");

        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setVerb("verb");
        List<SimpleActivity> firstRequest = flatActivityService.getActivities().getResults();
        assertThat(firstRequest.size(), is(0));
        SimpleActivity response = flatActivityService.addActivity(activity);
        assertThat(response.getId(), not(""));
        Assert.assertNotNull(response.getId());
        Assert.assertTrue(response.getId().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        streamClient.shutdown();
    }

    @Test
    public void shouldRemoveActivity() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldRemoveActivity");

        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setVerb("verb");
        List<SimpleActivity> firstRequest = flatActivityService.getActivities().getResults();
        assertThat(firstRequest.size(), is(0));
        SimpleActivity response = flatActivityService.addActivity(activity);
        List<SimpleActivity> secondRequest = flatActivityService.getActivities().getResults();
        assertThat(secondRequest.size(), is(1));

        streamClient.shutdown();
    }

    @Test
    public void shouldRemoveActivityByForeignId() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldRemoveActivityByForeignId");

        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        SimpleActivity activity = new SimpleActivity();
        activity.setActor("actor");
        activity.setObject("object");
        activity.setTarget("target");
        activity.setVerb("verb");
        List<SimpleActivity> firstRequest = flatActivityService.getActivities().getResults();
        assertThat(firstRequest.size(), is(0));
        flatActivityService.addActivity(activity);
        List<SimpleActivity> secondRequest = flatActivityService.getActivities().getResults();
        assertThat(secondRequest.size(), is(1));

        streamClient.shutdown();
    }

    @Test
    public void shouldGetActivitiesWithFilter() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);
        String userId = this.getTestUserId("shouldGetActivitiesWithFilter");
        Feed feed = streamClient.newFeed("user", userId);
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        flatActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build());
        streamClient.shutdown();
    }

    @Test(expected = InvalidOrMissingInputException.class)
    public void shouldGetInvalidOrMissingInputException() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("foo", "2");
        FlatActivityServiceImpl<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        flatActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build());
        streamClient.shutdown();
    }

    @Test(expected = AuthenticationFailedException.class)
    public void shouldGetAuthenticationFailed() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                                                                "foo");

        Feed feed = streamClient.newFeed("user", "2");
        feed.follow("user", "4");
        streamClient.shutdown();
    }

    @Test
    public void shouldGetActivitiesFromNotificationFeed() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("notification", "2");
        NotificationActivityServiceImpl<SimpleActivity> notificationActivityService =
                feed.newNotificationActivityService(SimpleActivity.class);
        StreamResponse<NotificationActivity<SimpleActivity>> response =
                notificationActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build(), true, true);
        streamClient.shutdown();
    }

    @Test
    public void shouldGetActivitiesFromAggregatedFeed() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        String userId = this.getTestUserId("2");
        Feed feed = streamClient.newFeed("aggregated", userId);
        AggregatedActivityServiceImpl<SimpleActivity> aggregatedActivityService =
                feed.newAggregatedActivityService(SimpleActivity.class);
        aggregatedActivityService.getActivities();
        streamClient.shutdown();
    }

    @Test
    public void shouldDeleteActivity() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), API_KEY,
                API_SECRET);

        Feed feed = streamClient.newFeed("user", "9");
        feed.deleteActivities(Arrays.asList("6d95a136-b2af-11e4-8080-80003ad855af",
                                                   "6d79af6c-b2af-11e4-8080-80003ad855af"));
        streamClient.shutdown();
    }
}
