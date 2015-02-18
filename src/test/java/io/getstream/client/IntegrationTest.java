package io.getstream.client;

import io.getstream.client.config.ClientConfiguration;
import io.getstream.client.config.StreamRegion;
import io.getstream.client.exception.AuthenticationFailedException;
import io.getstream.client.exception.InvalidOrMissingInputException;
import io.getstream.client.exception.StreamClientException;
import io.getstream.client.model.activities.SimpleActivity;
import io.getstream.client.model.beans.FeedFollow;
import io.getstream.client.model.beans.MarkedActivity;
import io.getstream.client.model.feeds.Feed;
import io.getstream.client.model.filters.FeedFilter;
import io.getstream.client.service.AggregatedActivityService;
import io.getstream.client.service.FlatActivityService;
import io.getstream.client.service.NotificationActivityService;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntegrationTest {

	@BeforeClass
	public static void setLog() {
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
	}

    @Test
    public void shouldGetFollowers() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
                                                            "245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");
        Feed feed = streamClient.newFeed("user", "2");
		assertThat(feed.getFollowers().size(), is(2));
		streamClient.shutdown();
    }

	@Ignore
	public void shouldGetFollowersFromDifferentRegion() throws IOException, StreamClientException {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setRegion(StreamRegion.US_WEST);
		StreamClient streamClient = new StreamClientImpl(clientConfiguration, "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
		assertThat(feed.getFollowers().size(), is(2));
		streamClient.shutdown();
	}

	@Test
    public void shouldGetFollowing() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
                                                            "245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
		List<FeedFollow> following = feed.getFollowing();
		assertThat(following.size(), is(3));
		streamClient.shutdown();
    }

    @Test
    public void shouldFollow() throws IOException, StreamClientException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
                                                            "245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");
        Feed feed = streamClient.newFeed("user", "2");
        feed.follow("user:4");
		streamClient.shutdown();
    }

    @Test
    public void shouldUnfollow() throws IOException, StreamClientException, InterruptedException {
        StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
                                                            "245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");

		List<FeedFollow> following = feed.getFollowing();
		assertThat(following.size(), is(3));

        feed.unfollow("user:4");

		List<FeedFollow> followingAgain = feed.getFollowing();
		assertThat(followingAgain.size(), is(2));
		streamClient.shutdown();
    }

	@Test
	public void shouldGetActivities() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
        FlatActivityService<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
		for (SimpleActivity activity : flatActivityService.getActivities()) {
			assertThat(activity.getId(), containsString("11e4-8080"));
		}
		streamClient.shutdown();
	}

	@Test
	public void shouldAddActivity() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
        FlatActivityService<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
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
	public void shouldGetActivitiesWithFilter() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
        FlatActivityService<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        flatActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build());
		streamClient.shutdown();
	}

	@Test(expected = InvalidOrMissingInputException.class)
	public void shouldGetInvalidOrMissingInputException() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("foo", "2");
        FlatActivityService<SimpleActivity> flatActivityService = feed.newFlatActivityService(SimpleActivity.class);
        flatActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build());
		streamClient.shutdown();
	}

	@Test(expected = AuthenticationFailedException.class)
	public void shouldGetAuthenticationFailed() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"foo");

        Feed feed = streamClient.newFeed("user", "2");
        feed.follow("user:4");
		streamClient.shutdown();
	}

	@Test
	public void shouldGetActivitiesFromNotificationFeed() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

        Feed feed = streamClient.newFeed("user", "2");
        NotificationActivityService<SimpleActivity> notificationActivityService =
                feed.newNotificationActivityService(SimpleActivity.class);
        notificationActivityService.getActivities(new FeedFilter.Builder().withLimit(50).withOffset(2).build(),
                                  null,
                                  new MarkedActivity.Builder().withActivityId("user:1").withActivityId("user:2").build());
		streamClient.shutdown();
	}

	@Test
	public void shouldGetActivitiesFromAggregatedFeed() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

		Feed feed = streamClient.newFeed("user", "2");
		AggregatedActivityService<SimpleActivity> aggregatedActivityService =
				feed.newAggregatedActivityService(SimpleActivity.class);
		aggregatedActivityService.getActivities();
		streamClient.shutdown();
	}

	@Test
	public void shouldDeleteActivity() throws IOException, StreamClientException {
		StreamClient streamClient = new StreamClientImpl(new ClientConfiguration(), "nfq26m3qgfyp",
				"245nvvjm49s3uwrs5e4h3gadsw34mnwste6v3rdnd69ztb35bqspvq8kfzt9v7h2");

		Feed feed = streamClient.newFeed("user", "9");
		feed.deleteActivities(Arrays.asList("6d95a136-b2af-11e4-8080-80003ad855af",
				"6d79af6c-b2af-11e4-8080-80003ad855af"));
		streamClient.shutdown();
	}
}
