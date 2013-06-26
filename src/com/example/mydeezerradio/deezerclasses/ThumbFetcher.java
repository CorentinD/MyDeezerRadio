package com.example.mydeezerradio.deezerclasses;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

/**
 * A class to asynchronously fetch thumbs associated with a list of HasThumbnail
 * items. It has been designed to be used easily inside the UI layer.
 * 
 * @author Deezer
 */
public class ThumbFetcher {

	private static final int BUFFERED_OUTPUT_STREAM_BUFFER_SIZE = 10000;
	/** Log cat tag. */
	private static final String LOG_TAG = "SimpleDeezerModel";
	/**
	 * RAM cache of thumbs. Associates a url to an image, shared by all
	 * instances.
	 */
	private static Map<String, Drawable> cacheThumbs = new HashMap<String, Drawable>();

	/** The context to get the cache folder from. */
	private Context context;

	/**
	 * Creates a thumbFetcher for a list of thumbnails to download. The
	 * thumbnails are cached in the cache folder of a context .
	 * 
	 * @param context
	 *            the given context, thumbnails will be stored in its cache
	 *            folder.
	 */
	public ThumbFetcher(Context context) {
		this.context = context;
	}// cons

	/**
	 * Starts fetching thumbails of a list thumbnailable items. It will run
	 * asynchronously in a background thread, cache thumbnails and call
	 * thumbFetcher methods on the UI thread.
	 * 
	 * @param listThumbnailable
	 *            the list of items whose thumbnails are to be downloaded.
	 * @param thumbFetcher
	 *            a listener that will be notified of thumbloaded events, on th
	 *            UI thread.
	 */
	public void startFetchingThumbnails(
			List<? extends Thumbnailable> listThumbnailable,
			ThumbFetcherListener thumbFetcherListener) {
		if (listThumbnailable == null || listThumbnailable.isEmpty()) {
			return;
		}// if
		if (thumbFetcherListener == null) {
			throw new IllegalArgumentException(
					"thumbFetcherListener can't be null.");
		}// if
		Thumbnailable[] params = new Thumbnailable[listThumbnailable.size()];
		new FetchTumbnailsTask(thumbFetcherListener).execute(listThumbnailable
				.toArray(params));
	}// met

	/**
	 * Starts fetching a thumbnailable item. It will run asynchronously in a
	 * background thread, cache thumbnails and call thumbFetcher methods on the
	 * UI thread.
	 * 
	 * @param thumbnailable
	 *            an item whose thumbnail is to be downloaded.
	 * @param thumbFetcher
	 *            a listener that will be notified of thumbloaded events, on th
	 *            UI thread.
	 */
	public void startFetchingThumbnails(Thumbnailable thumbnailable,
			ThumbFetcherListener thumbFetcherListener) {
		List<Thumbnailable> listThumbnailable = new ArrayList<Thumbnailable>();
		listThumbnailable.add(thumbnailable);
		this.startFetchingThumbnails(listThumbnailable, thumbFetcherListener);
	}// met

	/**
	 * Returns the thumbnail assoociated to a HasThumbnail item. Null is not
	 * available.
	 * 
	 * @param a
	 *            the item that is represented by a thumbnail.
	 * @return the thumbnail assoociated to a HasThumbnail item. Null is not
	 *         available.
	 */
	public Drawable getThumbnail(Thumbnailable a) {
		return cacheThumbs.get(a.getThumbnailUrl());
	}// met

	/**
	 * Put a thumbUri content in cache and returns its drawable content.
	 * 
	 * @param thumbUri
	 *            the thumbUri to download.
	 * @return the drawable point by thumbUri.
	 * @throws MalformedURLException
	 *             if thumbUri is not a valid Uri.
	 * @throws IOException
	 *             if an error happens during download or cache writing.
	 */
	private Drawable cacheUriAndCreateDrawable(String thumbUri)
			throws MalformedURLException, IOException {
		Drawable d;
		// create cache file
		String fileName = createCacheFileName(thumbUri);
		File cacheFile = new File(context.getCacheDir(), fileName);
		Log.d(LOG_TAG, "Fetching " + thumbUri);
		if (!cacheFile.exists()) {
			downloadToCache(thumbUri, cacheFile);
		}// if

		// create bitmap from downloaded bytes and put it in ram cache
		Log.d(LOG_TAG, thumbUri + " in cache");
		d = getDrawableFromCache(cacheFile);
		return d;
	}// met

	/**
	 * Create the fileName of the cache file associated to a thumbUri.
	 * 
	 * @param thumbUri
	 *            the uri to cache.
	 * @return the fileName of the cache file.
	 */
	private String createCacheFileName(String thumbUri) {
		return "thumb-" + thumbUri.hashCode() + "-"
				+ thumbUri.substring(thumbUri.lastIndexOf('/') + 1);
	}// met

	/**
	 * Reads a drawable from cache file.
	 * 
	 * @param cacheFile
	 *            the file containing the drawable.
	 * @return the drawable in cacheFile.
	 * @throws FileNotFoundException
	 *             if cacheFile doesn't exist.
	 */
	private Drawable getDrawableFromCache(File cacheFile) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(cacheFile), 9000);
			return Drawable.createFromStream(bis, "src name");
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Can't create drawable from cache file :"
					+ cacheFile, e);
			return null;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Can't close cache input stream", e);
				}// catch
			}// if
		}// fin
	}// met

	/**
	 * Downloads a given uri to cache.
	 * 
	 * @param thumbUri
	 *            the uri of the thumb to download.
	 * @param cacheFile
	 *            the cache file destination.
	 * @throws IOException
	 *             if an IO error happens during download or copy to cache.
	 * @throws MalformedURLException
	 *             if the thumbUri is malformed.
	 */
	private void downloadToCache(String thumbUri, File cacheFile)
			throws MalformedURLException, IOException {
		Log.v(LOG_TAG,
				"Fetching " + thumbUri + " and caching in "
						+ cacheFile.getAbsolutePath());
		InputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			// internal buffer for download.
			final byte[] buffer = new byte[5000];
			bis = (InputStream) new URL(thumbUri).getContent();
			if (bis == null) {
				return;
			}// if
			bos = new BufferedOutputStream(new FileOutputStream(cacheFile),
					BUFFERED_OUTPUT_STREAM_BUFFER_SIZE);
			int read = 0;
			while ((read = bis.read(buffer)) != -1)
				bos.write(buffer, 0, read);
			Log.d(LOG_TAG, thumbUri + " fetched");
		} finally {
			if (bis != null) {
				bis.close();
			}// if
			if (bos != null) {
				bos.close();
			}// if
		}// fin
	}// met

	private class FetchTumbnailsTask extends
			AsyncTask<Thumbnailable, Thumbnailable, Void> {
		/** A listener of thumbnails downloading. */
		private ThumbFetcherListener thumbFetcherListner = null;

		private FetchTumbnailsTask(ThumbFetcherListener thumbFetcherListner) {
			this.thumbFetcherListner = thumbFetcherListner;
		}// met

		/*
		 * Gets the thumbnails associated with a list of HasThumbnail objects.
		 * All thumbnails will be downloaded and cached in the app's cache
		 * folder.
		 * 
		 * @param listThumbable the list of objects that have a thumbnail to
		 * download.
		 */
		@Override
		protected Void doInBackground(Thumbnailable... listThumbable) {

			for (Thumbnailable thumbable : listThumbable) {
				// android guidelines suggest to check the cancel state
				if (isCancelled()) {
					return null;
				}// if

				String thumbUri = thumbable.getThumbnailUrl();
				Log.d(LOG_TAG, "Getting " + thumbUri);
				if (thumbUri == null || thumbUri.length() == 0) {
					Log.d(LOG_TAG, thumbUri + " is null or empty. Passed");
					continue;
				}// if

				// check in ram cache
				Drawable d = cacheThumbs.get(thumbUri);

				if (d == null) {
					// if not present, download
					try {
						d = cacheUriAndCreateDrawable(thumbUri);
						if (d != null) {
							cacheThumbs.put(thumbUri, d);
						}// if
					} catch (IOException e) {
						Log.e(LOG_TAG, "Error happened during download of "
								+ thumbUri, e);
					}// catch
				}// if

				// if in cache now
				if (d != null) {
					publishProgress(thumbable);
				}// if
			}// for
			return null;
		}// met

		@Override
		protected void onProgressUpdate(Thumbnailable... progress) {
			Thumbnailable thumbable = progress[0];
			// fire events for Observable-Observer design pattern
			// listeners will have to update UI.
			fireThumbLoaded(thumbable);
		}// met

		/**
		 * Notifies a listener that the thumbnail of an artist is now available.
		 * 
		 * @param thumbnailable
		 *            the thumbnailable whose thumbnail is now available.
		 */
		protected void fireThumbLoaded(Thumbnailable thumbnailable) {
			thumbFetcherListner.thumbLoaded(thumbnailable);
		}// met
	}// inner class

}// class
