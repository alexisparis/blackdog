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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListCommonOps;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.SongStats;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.jvnet.substance.SubstanceDefaultComboBoxRenderer;
import org.jvnet.substance.SubstanceDefaultTableCellRenderer;

/**
 * This class defines all columns than can be viewed in Play List
 * 
 * @author fleax
 * 
 */
public class PlayListColumns {

	/**
	 * Enumeration containing all available columns
	 */
	public enum PlayListColumn {
		PLAYING_ID, FAVORITE_ID, TRACK_ID, TITLE_ID, TYPE_ID, ARTIST_ID, ALBUM_ID, ALBUM_ARTIST_ID, COMPOSER_ID, GENRE_ID, YEAR_ID, LENGTH_ID, SCORE_ID, FILENAME_ID, PATH_ID, SIZE_ID, BITRATE_ID, FREQUENCY_ID, TIMES_PLAYED_ID
	}

	private static Map<PlayListColumn, Column> columns;

	/**
	 * Getter for Album artist Column
	 */
	private static Column getAlbumArtistColumn() {
		Column ALBUM_ARTIST = new Column(PlayListColumn.ALBUM_ARTIST_ID, "ALBUM_ARTIST", String.class) {
			private static final long serialVersionUID = -6260682057084442622L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						if (ao1.getAlbumArtist().equals(ao2.getAlbumArtist()))
							return ao1.getTrackNumber().compareTo(ao2.getTrackNumber());
						return ao1.getAlbumArtist().compareTo(ao2.getAlbumArtist());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return album artist
				return audioObject.getAlbumArtist();
			}
		};
		ALBUM_ARTIST.setVisible(false);
		return ALBUM_ARTIST;
	}

	/**
	 * Getter for Album Column
	 */
	private static Column getAlbumColumn() {
		Column ALBUM = new Column(PlayListColumn.ALBUM_ID, "ALBUM", String.class) {
			private static final long serialVersionUID = -6260682057084442622L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						if (ao1.getAlbum().equals(ao2.getAlbum()))
							return ao1.getTrackNumber().compareTo(ao2.getTrackNumber());
						return ao1.getAlbum().compareTo(ao2.getAlbum());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return album
				return audioObject.getAlbum();
			}
		};
		ALBUM.setVisible(true);
		return ALBUM;
	}

	/**
	 * Getter for Artist Column
	 */
	private static Column getArtistColumn() {
		Column ARTIST = new Column(PlayListColumn.ARTIST_ID, "ARTIST", String.class) {
			private static final long serialVersionUID = 1997593498590614982L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ao1.getArtist().compareTo(ao2.getArtist());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return artist
				return audioObject.getArtist();
			}
		};
		ARTIST.setVisible(true);
		return ARTIST;
	}

	/**
	 * Getter for Bitrate column
	 */
	private static Column getBitrateColumn() {
		Column BITRATE = new Column(PlayListColumn.BITRATE_ID, "BITRATE", String.class) {
			private static final long serialVersionUID = 0L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return Long.valueOf(ao1.getBitrate()).compareTo(Long.valueOf(ao2.getBitrate()));
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return bitrate
				if (audioObject.getBitrate() > 0)
					return StringUtils.getString(Long.toString(audioObject.getBitrate()), " Kbps");
				return "";
			}
		};
		BITRATE.setWidth(100);
		BITRATE.setVisible(false);
		BITRATE.setAlignment(SwingConstants.CENTER);
		return BITRATE;
	}

	public static Map<PlayListColumn, Column> getColumns() {
		if (columns == null) {
			// Try to get configuration saved
			Map<String, ColumnBean> columnsBeans = Kernel.getInstance().state.getColumns();

			columns = getColumnsByDefault();

			// Apply configuration
			if (columnsBeans != null) {
				for (PlayListColumn column : columns.keySet()) {
					Column c = columns.get(column);
					ColumnBean bean = columnsBeans.get(column.name());
					if (bean != null)
						c.applyColumnBean(bean);
				}
			} else {
				storeCurrentColumnSettings();
			}
		}
		return columns;
	}

	/**
	 * Returns all columns by default
	 * 
	 * @return
	 */
	public static Map<PlayListColumn, Column> getColumnsByDefault() {
		Map<PlayListColumn, Column> result = new HashMap<PlayListColumn, Column>();
		result.put(PlayListColumn.PLAYING_ID, getPlayingColumn());
		result.put(PlayListColumn.FAVORITE_ID, getFavoriteColumn());
		result.put(PlayListColumn.TYPE_ID, getTypeColumn());
		result.put(PlayListColumn.TRACK_ID, getTrackColumn());
		result.put(PlayListColumn.TITLE_ID, getTitleColumn());
		result.put(PlayListColumn.ARTIST_ID, getArtistColumn());
		result.put(PlayListColumn.ALBUM_ID, getAlbumColumn());
		result.put(PlayListColumn.ALBUM_ARTIST_ID, getAlbumArtistColumn());
		result.put(PlayListColumn.COMPOSER_ID, getComposerColumn());
		result.put(PlayListColumn.GENRE_ID, getGenreColumn());
		result.put(PlayListColumn.YEAR_ID, getYearColumn());
		result.put(PlayListColumn.LENGTH_ID, getLengthColumn());
		result.put(PlayListColumn.SCORE_ID, getScoreColumn());
		result.put(PlayListColumn.FILENAME_ID, getFileNameColumn());
		result.put(PlayListColumn.PATH_ID, getPathColumn());
		result.put(PlayListColumn.SIZE_ID, getSizeColumn());
		result.put(PlayListColumn.BITRATE_ID, getBitrateColumn());
		result.put(PlayListColumn.FREQUENCY_ID, getFrequencyColumn());
		result.put(PlayListColumn.TIMES_PLAYED_ID, getTimesPlayedColumn());
		return result;
	}

	/**
	 * Returns columns for selection -> Don't return favorite column
	 */
	public static List<Column> getColumnsForSelection() {
		Map<PlayListColumn, Column> cols = getColumns();
		List<Column> result = new ArrayList<Column>(cols.values());
		result.remove(getPlayingColumn());
		result.remove(cols.get(PlayListColumn.PLAYING_ID));
		return result;
	}

	/**
	 * Returns columns in order
	 */
	public static List<Column> getColumnsOrdered() {
		List<Column> result = new ArrayList<Column>(getColumns().values());
		Collections.sort(result);
		return result;
	}

	/**
	 * Getter for Composer Column
	 */
	private static Column getComposerColumn() {
		Column COMPOSER = new Column(PlayListColumn.COMPOSER_ID, LanguageTool.getString("COMPOSER"), String.class) {

			private static final long serialVersionUID = 6602324403487631368L;

			@Override
			public String getColumnName() {
				return LanguageTool.getString("COMPOSER");
			}

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ao1.getComposer().compareTo(ao2.getComposer());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				return audioObject.getComposer();
			}
		};
		COMPOSER.setVisible(true);
		return COMPOSER;
	}

	/**
	 * Getter for Favorite Column
	 */
	private static Column getFavoriteColumn() {
		Column FAVORITE = new Column(PlayListColumn.FAVORITE_ID, "", ImageIcon.class) {
			private static final long serialVersionUID = 4109392802052574533L;

			@Override
			public String getColumnName() {
				return LanguageTool.getString("FAVORITES");
			}

			@Override
			public Comparator<AudioObject> getComparator() {
				// No comparator
				return null;
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return image
				if (audioObject instanceof Radio)
					return ImageLoader.EMPTY;
				if (audioObject instanceof PodcastFeedEntry)
					return ImageLoader.EMPTY;
				return FavoritesHandler.getInstance().getFavoriteSongs().contains(audioObject) ? ImageLoader.FAVORITE : ImageLoader.EMPTY;
			}
		};
		FAVORITE.setResizable(false);
		FAVORITE.setWidth(18);
		FAVORITE.setVisible(true);
		return FAVORITE;
	}

	/**
	 * Getter for FileName column
	 */
	private static Column getFileNameColumn() {
		Column FILENAME = new Column(PlayListColumn.FILENAME_ID, "FILE", String.class) {
			private static final long serialVersionUID = -7196191399518593090L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ((AudioFile) ao1).getName().compareTo(((AudioFile) ao2).getName());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				if (audioObject instanceof Radio)
					return "";
				if (audioObject instanceof PodcastFeedEntry)
					return "";
				return ((AudioFile) audioObject).getName();
			}
		};
		FILENAME.setWidth(250);
		FILENAME.setVisible(false);
		return FILENAME;
	}

	/**
	 * Getter for Frequency column
	 */
	private static Column getFrequencyColumn() {
		Column FREQUENCY = new Column(PlayListColumn.FREQUENCY_ID, "FREQUENCY", String.class) {
			private static final long serialVersionUID = 0L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return Integer.valueOf(ao1.getFrequency()).compareTo(Integer.valueOf(ao2.getFrequency()));
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return FREQUENCY
				if (audioObject.getFrequency() > 0)
					return StringUtils.getString(Integer.toString(audioObject.getFrequency()), " Hz");
				return "";
			}
		};
		FREQUENCY.setWidth(100);
		FREQUENCY.setVisible(false);
		FREQUENCY.setAlignment(SwingConstants.CENTER);
		return FREQUENCY;
	}

	/**
	 * Getter for Genre Column
	 */
	private static Column getGenreColumn() {
		Column GENRE = new Column(PlayListColumn.GENRE_ID, "GENRE", String.class) {
			private static final long serialVersionUID = 1850661531083681051L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						if (ao1.getGenre().equals(ao2.getGenre())) {
							if (ao1.getArtist().equals(ao2.getArtist())) {
								if (ao1.getAlbum().equals(ao2.getAlbum()))
									return ao1.getTrackNumber().compareTo(ao2.getTrackNumber());
								return ao1.getAlbum().compareTo(ao2.getAlbum());
							}
							return ao1.getArtist().compareTo(ao2.getArtist());
						}
						return ao1.getGenre().compareTo(ao2.getGenre());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return genre
				return audioObject.getGenre();
			}
		};
		GENRE.setVisible(true);
		return GENRE;
	}

	/**
	 * Getter for Length Column
	 */
	private static Column getLengthColumn() {
		Column LENGTH = new Column(PlayListColumn.LENGTH_ID, "DURATION", String.class) {
			private static final long serialVersionUID = -9125064451283868817L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return Long.valueOf(ao1.getDuration()).compareTo(Long.valueOf(ao2.getDuration()));
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return length
				if (audioObject instanceof Radio)
					return "";
				if (audioObject instanceof PodcastFeedEntry && ((PodcastFeedEntry) audioObject).getDuration() <= 0)
					return "-";
				return TimeUtils.seconds2String(audioObject.getDuration());
			}
		};
		LENGTH.setWidth(100);
		LENGTH.setVisible(true);
		LENGTH.setAlignment(SwingConstants.CENTER);
		return LENGTH;
	}

	/**
	 * Getter for Path column
	 */
	private static Column getPathColumn() {
		Column PATH = new Column(PlayListColumn.PATH_ID, "LOCATION", String.class) {
			private static final long serialVersionUID = 1968647342075505057L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						String p1 = "";
						String p2 = "";
						if (ao1 instanceof AudioFile) {
							p1 = ((AudioFile) ao1).getParentFile().getAbsolutePath();
						}
						if (ao2 instanceof AudioFile) {
							p2 = ((AudioFile) ao2).getParentFile().getAbsolutePath();
						}
						return p1.compareTo(p2);
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				if (audioObject instanceof Radio)
					return ((Radio) audioObject).getUrl();
				if (audioObject instanceof PodcastFeedEntry)
					return ((PodcastFeedEntry) audioObject).getUrl();
				return ((AudioFile) audioObject).getParentFile().getAbsolutePath();
			}
		};
		PATH.setWidth(350);
		PATH.setVisible(false);
		return PATH;
	}

	/**
	 * Getter for Playing Column
	 * 
	 * @return
	 */
	private static Column getPlayingColumn() {
		Column PLAYING = new Column(PlayListColumn.PLAYING_ID, "", Integer.class) {
			private static final long serialVersionUID = 8526676216361302614L;

			@Override
			public Comparator<AudioObject> getComparator() {
				// No comparator
				return null;
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				return 1;
			}
		};
		PLAYING.setResizable(false);
		PLAYING.setWidth(16);
		PLAYING.setVisible(true);
		return PLAYING;
	}

	/**
	 * Getter for Score column
	 */
	private static Column getScoreColumn() {
		Column STARS = new Column(PlayListColumn.SCORE_ID, "SCORE", Integer.class) {
			private static final long serialVersionUID = 3781963272944210328L;

			@Override
			public TableCellEditor getCellEditor() {
				JComboBox comboBox = new JComboBox(new Object[] { 0, 1, 2, 3, 4, 5 });
				comboBox.setRenderer(new SubstanceDefaultComboBoxRenderer(comboBox) {
					private static final long serialVersionUID = 0L;

					@Override
					public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
						JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
						label.setText("");

						switch ((Integer) value) {
						case 0:
							label.setIcon(ImageLoader.EMPTY);
							break;
						case 1:
							label.setIcon(ImageLoader.ONE_STAR);
							break;
						case 2:
							label.setIcon(ImageLoader.TWO_STAR);
							break;
						case 3:
							label.setIcon(ImageLoader.THREE_STAR);
							break;
						case 4:
							label.setIcon(ImageLoader.FOUR_STAR);
							break;
						case 5:
							label.setIcon(ImageLoader.FIVE_STAR);
							break;
						}

						return label;
					}
				});

				return new DefaultCellEditor(comboBox);
			}

			@Override
			public TableCellRenderer getCellRenderer() {
				return new SubstanceDefaultTableCellRenderer() {
					private static final long serialVersionUID = 0L;

					@Override
					public Component getTableCellRendererComponent(JTable arg0, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
						JLabel label = (JLabel) super.getTableCellRendererComponent(arg0, "", arg2, arg3, arg4, arg5);
						switch ((Integer) value) {
						case 0:
							label.setIcon(ImageLoader.EMPTY);
							break;
						case 1:
							label.setIcon(ImageLoader.ONE_STAR);
							break;
						case 2:
							label.setIcon(ImageLoader.TWO_STAR);
							break;
						case 3:
							label.setIcon(ImageLoader.THREE_STAR);
							break;
						case 4:
							label.setIcon(ImageLoader.FOUR_STAR);
							break;
						case 5:
							label.setIcon(ImageLoader.FIVE_STAR);
							break;
						}
						return label;
					}
				};
			}

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return -((Integer) ao1.getStars()).compareTo(ao2.getStars());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				return audioObject.getStars();
			}

			@Override
			public void setValueFor(AudioObject audioObject, Object value) {
				audioObject.setStars((Integer) value);

				// After setting score of an AudioFile, refresh playlist, as the same song can be duplicated
				PlayListCommonOps.refreshPlayList();
			}
		};
		STARS.setWidth(100);
		STARS.setVisible(true);
		STARS.setEditable(true);
		return STARS;
	}

	/**
	 * Getter for Size column
	 */
	private static Column getSizeColumn() {
		Column SIZE = new Column(PlayListColumn.SIZE_ID, "SIZE", String.class) {
			private static final long serialVersionUID = -6441837602801955473L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						long l1 = 0;
						long l2 = 0;
						if (ao1 instanceof AudioFile) {
							l1 = ((AudioFile) ao1).length();
						}
						if (ao2 instanceof AudioFile) {
							l2 = ((AudioFile) ao2).length();
						}
						return Long.valueOf(l1).compareTo(l2);
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				if (audioObject instanceof Radio)
					return "";
				if (audioObject instanceof PodcastFeedEntry)
					return "";
				return StringUtils.fromByteToMegaOrGiga(((AudioFile) audioObject).length());
			}
		};
		SIZE.setWidth(100);
		SIZE.setVisible(false);
		SIZE.setAlignment(SwingConstants.CENTER);
		return SIZE;
	}

	/**
	 * Getter for Frequency column
	 */
	private static Column getTimesPlayedColumn() {
		Column TIMES_PLAYED = new Column(PlayListColumn.TIMES_PLAYED_ID, "TIMES_PLAYED", String.class) {
			private static final long serialVersionUID = 0L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						int times1 = 0;
						int times2 = 0;
						if (ao1 instanceof AudioFile) {
							SongStats stats1 = RepositoryHandler.getInstance().getSongStatistics((AudioFile) ao1);
							times1 = stats1 != null ? stats1.getTimesPlayed() : 0;
						}
						if (ao2 instanceof AudioFile) {
							SongStats stats2 = RepositoryHandler.getInstance().getSongStatistics((AudioFile) ao2);
							times2 = stats2 != null ? stats2.getTimesPlayed() : 0;
						}
						return ((Integer) times1).compareTo(times2);
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				if (audioObject instanceof Radio)
					return "";
				if (audioObject instanceof PodcastFeedEntry)
					return "";
				// Return times played
				SongStats stats = RepositoryHandler.getInstance().getSongStatistics((AudioFile) audioObject);
				if (stats != null) {
					if (stats.getTimesPlayed() > 0)
						return Integer.toString(stats.getTimesPlayed());
				}
				return "";
			}
		};
		TIMES_PLAYED.setWidth(100);
		TIMES_PLAYED.setVisible(false);
		TIMES_PLAYED.setAlignment(SwingConstants.CENTER);
		return TIMES_PLAYED;
	}

	/**
	 * Getter for Title Column
	 */
	private static Column getTitleColumn() {
		Column TITLE = new Column(PlayListColumn.TITLE_ID, "TITLE", AudioObject.class) {
			private static final long serialVersionUID = 5523169700837457324L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ao1.getTitleOrFileName().compareTo(ao2.getTitleOrFileName());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return the file
				return audioObject;
			}
		};
		TITLE.setVisible(true);
		return TITLE;
	}

	/**
	 * Getter for Track Column
	 * 
	 * @return
	 */
	private static Column getTrackColumn() {
		Column TRACK = new Column(PlayListColumn.TRACK_ID, "TRACK", String.class) {
			private static final long serialVersionUID = -557278151346168344L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ao1.getTrackNumber().compareTo(ao2.getTrackNumber());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return track number or empty string
				int track = audioObject.getTrackNumber();
				return track > 0 ? track : "";
			}
		};
		TRACK.setWidth(40);
		TRACK.setVisible(true);
		TRACK.setAlignment(SwingConstants.CENTER);
		return TRACK;
	}

	/**
	 * Getter for Type Column
	 * 
	 * @return
	 */
	private static Column getTypeColumn() {
		Column TYPE = new Column(PlayListColumn.TYPE_ID, "", ImageIcon.class) {
			private static final long serialVersionUID = -4523119624106967074L;

			@Override
			public String getColumnName() {
				return LanguageTool.getString("TYPE");
			}

			@Override
			public Comparator<AudioObject> getComparator() {
				// No comparator
				return null;
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				if (audioObject instanceof AudioFile) {
					return ImageLoader.AUDIO_FILE_LITTLE;
				} else if (audioObject instanceof Radio) {
					return ImageLoader.RADIO_LITTLE;
				} else if (audioObject instanceof PodcastFeedEntry) {
					return ImageLoader.RSS_LITTLE;
				} else {
					return null;
				}
			}
		};
		TYPE.setResizable(false);
		TYPE.setWidth(16);
		TYPE.setVisible(true);
		TYPE.setAlignment(SwingConstants.CENTER);
		return TYPE;
	}

	/**
	 * Getter for Year Column
	 */
	private static Column getYearColumn() {
		Column YEAR = new Column(PlayListColumn.YEAR_ID, "YEAR", String.class) {
			private static final long serialVersionUID = -7196191399518593090L;

			@Override
			public Comparator<AudioObject> getComparator() {
				return new Comparator<AudioObject>() {
					@Override
					public int compare(AudioObject ao1, AudioObject ao2) {
						return ao1.getYear().compareTo(ao2.getYear());
					}
				};
			}

			@Override
			public Object getValueFor(AudioObject audioObject) {
				// Return year
				return audioObject.getYear();
			}
		};
		YEAR.setWidth(100);
		YEAR.setAlignment(SwingConstants.CENTER);
		YEAR.setVisible(false);
		return YEAR;
	}

	public static void storeCurrentColumnSettings() {
		// Get ColumnsBean from default columns and store it
		HashMap<String, ColumnBean> newColumnsBeans = new HashMap<String, ColumnBean>();
		for (PlayListColumn column : columns.keySet()) {
			Column c = columns.get(column);
			newColumnsBeans.put(column.name(), c.getColumnBean());
		}
		Kernel.getInstance().state.setColumns(newColumnsBeans);
	}

}
