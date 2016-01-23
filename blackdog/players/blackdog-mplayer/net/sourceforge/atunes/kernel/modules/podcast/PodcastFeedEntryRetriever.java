/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.handlers.VisualHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is responsible for retrieving the entries from a podcast feed and
 * refreshing the user interface if necessary.
 */
public class PodcastFeedEntryRetriever extends Thread {

	/**
	 * The default podcast feed entries retrieval interval
	 */
	public static final long DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL = 180000;

	private static PodcastFeedEntryRetriever instance;
	private static Logger logger = new Logger();

	private Proxy proxy;

	/**
	 * The sleeping time for this thread
	 */
	private volatile long retrievalInterval;

	/**
	 * Constructor
	 */
	private PodcastFeedEntryRetriever() {
		this.setName("PodcastFeedEntryRetriever");

		// When upgrading from a previous version, retrievel interval can be 0
		long retrieval = Kernel.getInstance().state.getPodcastFeedEntriesRetrievalInterval();
		this.retrievalInterval = retrieval > 0 ? retrieval : DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

		try {
			this.proxy = NetworkUtils.getProxy(Kernel.getInstance().state.getProxy());
		} catch (UnknownHostException e) {
			logger.error(LogCategories.NETWORK, e);
		} catch (IOException e) {
			logger.error(LogCategories.NETWORK, e);
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the author node
	 */
	private static String getAuthorXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./author";
		case ATOM:
			return "./author/name";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the date node
	 */
	private static String getDateXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./pubDate";
		case ATOM:
			return "./updated";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the description node
	 */
	private static String getDescriptionXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./description";
		case ATOM:
			return "./summary";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the duration node
	 */
	private static String getDurationXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./duration";
		case ATOM:
			return "";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the entry nodes
	 */
	private static String getEntryXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "/rss/channel/item";
		case ATOM:
			return "/feed/entry";
		default:
			return "";
		}
	}

	/**
	 * @return The single instance of this class
	 */
	public static synchronized PodcastFeedEntryRetriever getInstance() {
		if (instance == null) {
			return instance = new PodcastFeedEntryRetriever();
		}
		return instance;
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the title node
	 */
	private static String getTitleXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./title";
		case ATOM:
			return "./title";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the type node
	 */
	private static String getTypeXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./enclosure/@type";
		case ATOM:
			return "./link[@rel='enclosure']/@type";
		default:
			return "";
		}
	}

	/**
	 * @param feedType
	 *            The podcast feed type
	 * @return The XPath expression for the url node
	 */
	private static String getURLXPathExpression(FeedType feedType) {
		switch (feedType) {
		case RSS:
			return "./enclosure/@url";
		case ATOM:
			return "./link[@rel='enclosure']/@href";
		default:
			return "";
		}
	}

	/**
	 * @return The sleepingTime
	 */
	public long getRetrievalInterval() {
		return retrievalInterval;
	}

	/**
	 * Retrieves Podcast Feed Entries and refreshes view asynchronously
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever#retrievePodcastFeedEntriesSynchronously()
	 */
	public void retrievePodcastFeedEntriesAsynchronously() {
		new Thread() {
			@Override
			public void run() {
				retrievePodcastFeedEntriesSynchronously();
			}
		}.start();
	}

	/**
	 * Retrieves Podcast Feed Entries and refreshes view synchronously <br />
	 * <br />
	 * <b>Note:</b> This method can block the execution of the calling thread
	 * for several seconds
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever#retrievePodcastFeedEntriesAsynchronously()
	 */
	public synchronized void retrievePodcastFeedEntriesSynchronously() {

		final List<PodcastFeed> podcastFeeds = PodcastFeedHandler.getInstance().getPodcastFeeds();
		final List<PodcastFeed> podcastFeedsWithNewEntries = new ArrayList<PodcastFeed>();

		for (final PodcastFeed podcastFeed : podcastFeeds) {
			try {
				Document feed = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(podcastFeed.getUrl(), proxy)));

				// Determine the feed type
				if (XMLUtils.evaluateXPathExpressionAndReturnNode("/rss", feed) != null) {
					podcastFeed.setFeedType(FeedType.RSS);
				} else {
					if (XMLUtils.evaluateXPathExpressionAndReturnNode("/feed", feed) != null) {
						podcastFeed.setFeedType(FeedType.ATOM);
					} else {
						logger.info(LogCategories.PODCAST, podcastFeed + " is not a rss or atom feed");
						continue;
					}
				}

				// Get entry nodes
				NodeList entries = XMLUtils.evaluateXPathExpressionAndReturnNodeList(getEntryXPathExpression(podcastFeed.getFeedType()), feed);

				final List<PodcastFeedEntry> newEntries = new ArrayList<PodcastFeedEntry>();
				for (int i = 0; i < entries.getLength(); i++) {

					String title = "";
					String url = "";
					String author = "";
					String description = "";
					Date date = null;
					long duration = 0;

					// Check if audio podcast feed entry
					Node typeNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getTypeXPathExpression(podcastFeed.getFeedType()), entries.item(i));
					if (typeNode == null || !typeNode.getTextContent().matches(".*audio.*")) {
						logger.info(LogCategories.PODCAST, StringUtils.getString("podcast feed entry is not from type audio: ", (typeNode != null ? typeNode.getTextContent()
								: "no type node")));
						continue;
					}

					// Get title of podcast entry
					Node titleNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getTitleXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (titleNode != null) {
						title = titleNode.getTextContent();
					}

					// Get url of podcast entry
					Node urlNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getURLXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (urlNode != null) {
						url = urlNode.getTextContent();
					} else {
						continue;
					}

					// Get Author of podcast entry
					Node authorNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getAuthorXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (authorNode != null) {
						author = authorNode.getTextContent();
					}

					// Get description of podcast entry
					Node descriptionNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getDescriptionXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (descriptionNode != null) {
						description = descriptionNode.getTextContent();
						description = description.replaceAll("\\<.*?\\>", "");
					}

					// Get date of podcast entry
					Node dateNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getDateXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (dateNode != null) {
						date = DateUtils.parseRFC822Date(dateNode.getTextContent());
						if (date == null) {
							date = DateUtils.parseRFC3339Date(dateNode.getTextContent());
						}
					}

					// Try to find out duration
					Node durationNode = XMLUtils.evaluateXPathExpressionAndReturnNode(getDurationXPathExpression(podcastFeed.getFeedType()), entries.item(i));

					if (durationNode != null) {
						String durationText = durationNode.getTextContent();
						// Transform "01:01:22" to seconds
						if (durationText != null) {
							String[] result = durationText.split(":");
							try {
								for (int j = result.length - 1; j >= 0; j--) {
									duration = duration + Integer.parseInt(result[j]) * (long) Math.pow(60, result.length - 1 - j);
								}
							} catch (NumberFormatException e) {
								duration = 0;
								logger.info(LogCategories.PODCAST, "could not extract podcast feed entry duration");
							}
						}
					}
					newEntries.add(new PodcastFeedEntry(title, author, url, description, date, duration, podcastFeed));
				}

				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						podcastFeed.setAsEntriesAndRemoveOldEntries(newEntries);
						if (podcastFeed.hasNewEntries())
							podcastFeedsWithNewEntries.add(podcastFeed);
					}
				});

			} catch (DOMException e) {
				logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
			} catch (IOException e) {
				logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
			} catch (InterruptedException e) {
				logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
			} catch (InvocationTargetException e) {
				logger.error(LogCategories.PODCAST, StringUtils.getString("Could not retrieve podcast feed entries from ", podcastFeed, ": ", e));
			}

		}

		// If there are new entries show a message and refresh view
		synchronized (podcastFeeds) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for (PodcastFeed podcastFeedWithNewEntries : podcastFeedsWithNewEntries) {
						// Check if podcast feed wasn't removed during retrieval
						if (podcastFeeds.contains(podcastFeedWithNewEntries)) {
							VisualHandler.getInstance().showMessage(LanguageTool.getString("NEW_PODCAST_ENTRIES"));
							// Remove "new" flag from podcasts
							for (PodcastFeed podcastFeed : podcastFeeds) {
								podcastFeed.markEntriesAsNotNew();
							}
							break;
						}
					}
				}
			});
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// refresh view
				ControllerProxy.getInstance().getNavigationController().refreshPodcastFeedTreeContent();
				logger.info(LogCategories.PODCAST, "Podcast feed entries retrieval done");
			}
		});

	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			retrievePodcastFeedEntriesSynchronously();
			try {
				Thread.sleep(retrievalInterval);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * @param newRetrievalInterval
	 *            The sleeping time for this thread to set (in milliseconds)
	 */
	public void setRetrievalInterval(long newRetrievalInterval) {
		if (newRetrievalInterval < 0) {
			throw new IllegalArgumentException("sleeping time must not be smaller than 0");
		}
		this.retrievalInterval = newRetrievalInterval;
	}

}
