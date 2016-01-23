/*
 * blackdog types : define kind of items maanged by blackdog
 *
 * Copyright (C) 2008 Alexis PARIS
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
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.type;

import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 *
 * Defines categories of audio stream
 *
 *
 *  WARNING : because of the hibernate mapping
 *  the name of the constants define by this enumeration
 *  must have its length lower or equals to 40
 *
 * @author alexis
 */
public enum AudioCategory
{   
    BLUES              ( "Blues"                   , "blues"          , new String[] { "blues", "0", "(0)" } ),
    CLASSIC_ROCK       ( "ClassicRock"             , "classic rock"   , new String[] { "classic rock", "1", "(1)" } ),
    COUNTRY            ( "Country"                 , "country"        , new String[] { "country", "2", "(2)" } ),
    DANCE              ( "Dance"                   , "dance"          , new String[] { "dance", "3", "(3)" } ),
    DISCO              ( "Disco"                   , "disco"          , new String[] { "disco", "4", "(4)" } ),
    FUNK               ( "Funk"                    , "funk"           , new String[] { "funk", "5", "(5)" } ),
    GRUNGE             ( "Grunge"                  , "grunge"         , new String[] { "grunge", "6", "(6)" } ),
    HIPHOP             ( "HipHop"                  , "hip-hop"        , new String[] { "hip-hop", "hip-hop", "7", "(7)" } ),
    JAZZ               ( "Jazz"                    , "jazz"           , new String[] { "jazz", "8", "(8)" } ),
    METAL              ( "Metal"                   , "metal"          , new String[] { "metal", "9", "(9)" } ),
    NEWAGE             ( "NewAge"                  , "new age"        , new String[] { "new age", "10", "(10)" } ),
    OLDIES             ( "Oldies"                  , "oldies"         , new String[] { "oldies", "11", "(11)", "76", "(76)" } ),
    UNKNOWN            ( "Unknown"                 , "unknown"        , new String[] { "unknown", "misc", "12", "(12)" } ),
    POP                ( "Pop"                     , "pop"            , new String[] { "pop", "pop-rock", "rockpop", "rock_pop", "rock/pop", "britpop", "13", "(13)" } ),
    R_AND_B            ( "R&B"                     , "r&b"            , new String[] { "r&b", "r & b", "14", "(14)" } ),
    RAP                ( "Rap"                     , "rap"            , new String[] { "rap", "15", "(15)" } ),
    REGGAE             ( "Reggae"                  , "reggae"         , new String[] { "reggae", "16", "(16)" } ),
    ROCK               ( "Rock"                    , "rock"           , new String[] { "rock", "ost/rock", "general rock", "17", "(17)" } ),
    TECHNO             ( "Techno"                  , "techno"         , new String[] { "techno", "18", "(18)" } ),
    INDUSTRIAL         ( "Industrial"              , "industrial"     , new String[] { "industrial", "19", "(19)" } ),
    ROCK_ALTERNATIVE   ( "RockAlter"               , "alternative"    , new String[] { "alternative", "alternative rock", "alt rock", "alt-rock", "alternrock", 
										       "general alternative", "avantgarde", "20", "(20)", "40", "(40)" } ),
    SKA		       ( "Ska"                     , "ska"            , new String[] { "ska", "21", "(21)" } ),
    DEATH_METAL	       ( "DeathMetal"              , "death metal"    , new String[] { "death metal", "death", "22", "(22)" } ),
    PRANKS	       ( "Pranks"                  , "pranks"         , new String[] { "pranks", "23", "(23)" } ),
    MOVIE	       ( "Movie"                   , "movie"          , new String[] { "movie", "bo", "bof", "soundtrack", "24", "(24)" } ),
    EUROTECHNO         ( "EuroTechno"              , "euro techno"    , new String[] { "euroTechno", "euro techno", "25", "(25)" } ),
    AMBIENT            ( "Ambient"                 , "ambient"        , new String[] { "ambient", "26", "(26)" } ),
    TRIPHOP            ( "TripHop"                 , "trip hop"       , new String[] { "trip hop", "triphop", "27", "(27)" } ),
    VOCAL              ( "Vocal"                   , "vocal"          , new String[] { "vocal", "28", "(28)" } ),
    JAZZ_FUNK          ( "JazzFunk"                , "jazz funk"      , new String[] { "Jazz-funk", "Jazz funk", "29", "(29)" } ),
    FUSION             ( "Fusion"                  , "fusion"         , new String[] { "fusion", "30", "(30)" } ),
    TRANCE             ( "Trance"                  , "trance"         , new String[] { "trance", "31", "(31)" } ),
    CLASSICAL          ( "Classical"               , "classical"      , new String[] { "classical", "classic", "32", "(32)" } ),
    INSTRUMENTAL       ( "Instrumental"            , "instrumental"   , new String[] { "instrumental", "33", "(33)" } ),
    ACID               ( "Acid"                    , "acid"           , new String[] { "acid", "34", "(34)" } ),
    HOUSE              ( "House"                   , "house"          , new String[] { "house", "35", "(35)" } ),
    VIDEO_GAME         ( "VideoGame"               , "game"           , new String[] { "game", "36", "(36)" } ),
    SAMPLE             ( "Sample"                  , "sample"         , new String[] { "sample", "37", "(37)" } ),
    GOSPEL             ( "Gospel"                  , "gospel"         , new String[] { "gospel", "38", "(38)" } ),
    NOISE              ( "Noise"                   , "noise"          , new String[] { "noise", "39", "(39)" } ),

    //ROCK_ALTERNATIVE2
    BASS               ( "Bass"                    , "bass"           , new String[] { "bass", "41", "(41)" } ),
    SOUL	       ( "Soul"                    , "soul"           , new String[] { "soul", "42", "(42)" } ),
    PUNK               ( "Punk"                    , "punk"           , new String[] { "punk", "punk rock", "punk-rock", "43", "(43)" } ),
    SPACE              ( "Space"                   , "space"          , new String[] { "space", "44", "(44)" } ),
    MEDIATIVE          ( "Mediative"               , "mediative"      , new String[] { "mediative", "45", "(45)" } ),
    POP_INST           ( "PopInst"                 , "pop inst"       , new String[] { "46", "(46)" } ),
    ROCK_INST          ( "RockInst"                , "rock inst"      , new String[] { "47", "(47)" } ),
    WORLD              ( "World"                   , "world"          , new String[] { "world", "48", "(48)" } ),
    GOTHIC             ( "Gothic"                  , "gothic"         , new String[] { "gothic", "gothique", "gothic rock", "49", "(49)" } ),
    DARKWARE           ( "DarkWare"                , "darkware"       , new String[] { "50", "(50)" } ),
    TECHNO_INDUS       ( "TechnoIndus"             , "techno indus"   , new String[] { "51", "(51)" } ),
    ELETRO	       ( "Electro"                 , "electro"        , new String[] { "electro", "electronic", "52", "(52)" } ),
    POP_FOLK           ( "PopFolk"                 , "folk"           , new String[] { "53", "(53)", "folk" } ),
    EURODANCE          ( "EuroDance"               , "eurodance"      , new String[] { "eurodance", "euro dance", "54", "(54)" } ),
    DREAM              ( "Dream"                   , "dream"          , new String[] { "dream", "55", "(55)" } ),

    //ROCK SUDISTE
    COMEDY             ( "Comedy"                  , "comedy"         , new String[] { "57", "(57)" } ),
    CULTE	       ( "Culte"		   , "culte"          , new String[] { "culte", "58", "(58)" } ),
    GANGSTA            ( "Gangsta"                 , "gangsta"        , new String[] { "gangsta", "59", "(59)" } ),

    //HITS
    //CHRISTIAN RAP
    POP_FUNK           ( "PopFunk"                 , "pop funk"       , new String[] { "62", "(62)" } ),
    JUNGLE	       ( "Jungle"		   , "jungle"         , new String[] { "jungle", "63", "(63)" } ),
    INDIAN             ( "Indian"                  , "indie"          , new String[] { "64", "(64)", "indie", "131", "(131)" } ),
    CABARET            ( "Cabaret"                 , "cabaret"        , new String[] { "65", "(65)" } ),
    NEW_WAVE           ( "NewWave"                 , "new wave"       , new String[] { "newwave", "new wave", "66", "(66)" } ),

    //PSYCHEDELIQUE
    RAVE               ( "Rave"                    , "rave"           , new String[] { "rave", "68", "(68)" } ),
    //SHOWTUNES
    //Bande annonce
    //LO-fi
    TRIBAL             ( "Tribal"                  , "tribal"         , new String[] { "72", "(72)" } ),
    // Acid punk
    // Acid jazz
    POLKA              ( "Polka"                   , "polka"          , new String[] { "polka", "75", "(75)" } ),

    //RETRO
    //THEATER
    ROCK_AND_ROLL      ( "RockAndRoll"             , "rock and roll"  , new String[] { "rockandroll", "rock and roll", "78", "(78)" } ),
    HARD_ROCK          ( "HardRock"                , "hard rock"      , new String[] { "hard rock", "hard-rock", "hardrock", "79", "(79)" } );
    /** code */
    private String code = null;
    
    /** preferred genre form */
    private String preferredGenreForm = null;

    /** array of String in lower case that can represents this category in ID3 standard */
    private String[] id3Forms = null;

    /** soft reference to the ResourceBundle linked to AudioCategory */
    private static SoftReference<ResourceBundle> rbReference = new SoftReference<ResourceBundle>( null );

    private AudioCategory( String code, String preferredGenreForm, String[] id3Forms )
    {
        this.code = code;
        this.id3Forms = id3Forms;
	this.preferredGenreForm = preferredGenreForm;
    }

    /** return the code of the category
     *        @return the code
     */
    public String getCode(  )
    {
        return this.code;
    }
    
    /** return the preferred genre form for the category
     *	@return a String
     */
    public String getPreferredGenreForm()
    {
	return this.preferredGenreForm;
    }

    /** return the label
     *  @return the label
     */
    public String label(  )
    {
        ResourceBundle rb = this.rbReference.get(  );

        if ( rb == null )
        {
            rb = ResourceBundle.getBundle( "org.blackdog.rc.i18n.AudioCategory" );
            this.rbReference = new SoftReference<ResourceBundle>( rb );
        }

        return rb.getString( this.code );
    }

    /** return the AudioCategory that most conveys to the given genre
     *  @param genre a String
     *  @return an AudioCategory
     */
    public static AudioCategory getAudioCategoryFor( String genre )
    {
	return getAudioCategoryForID3Genre(genre);
    }

    /** return the AudioCategory that most conveys to the given id3 genre String
     *  @param id3Genre a String
     *  @return an AudioCategory
     */
    public static AudioCategory getAudioCategoryForID3Genre( String id3Genre )
    {
        AudioCategory category = null;

        if ( ( id3Genre != null ) && ( id3Genre.trim(  ).length(  ) > 0 ) )
        {
            String genre = id3Genre.trim().toLowerCase();

            AudioCategory[] categories = AudioCategory.values(  );

            for ( int i = 0; i < categories.length && category == null; i++ )
            {
                AudioCategory current = categories[i];
		
		if ( category == null && current.code != null )
		{
		    if ( current.code.equals(id3Genre) )
		    {
			category = current;
		    }
		}
		
		if ( category == null && current.preferredGenreForm != null )
		{
		    if ( current.preferredGenreForm.equals(id3Genre) )
		    {
			category = current;
		    }
		}

		if ( category == null )
		{
		    String[] id3Forms = current.id3Forms;

		    if ( id3Forms != null )
		    {
			for ( int j = 0; j < id3Forms.length && category == null; j++ )
			{
			    String currentForm = id3Forms[j];

			    if ( currentForm != null )
			    {
				if ( currentForm.equals( genre ) )
				{
				    category = current;

				    break;
				}
			    }
			}
		    }
		}
            }
	    
	    if ( category == null )
	    {
		/* the genre could be '(68)rave' */
		int _start = genre.indexOf('(');
		int _end   = genre.indexOf(')');
		
		if ( _start == 0 && _end != genre.length() - 1 )
		{
		    /* decompose */
		    String inParenthesys = genre.substring(_start + 1, _end);
		    if ( inParenthesys != null )
		    {
			inParenthesys = inParenthesys.trim();
		    }
		    if ( inParenthesys != null && inParenthesys.length() > 0 )
		    {
			category = getAudioCategoryForID3Genre(inParenthesys);
		    }
		    
		    if ( category == null || AudioCategory.UNKNOWN.equals(category) )
		    {
			String outParenthesys = genre.substring(_end + 1);
			
			if ( outParenthesys != null )
			{
			    outParenthesys = outParenthesys.trim();
			}
			if ( outParenthesys != null && outParenthesys.length() > 0 )
			{
			    category = getAudioCategoryForID3Genre(outParenthesys);
			}
		    }
		}
	    }
        }

        if ( category == null )
        {
            category = AudioCategory.UNKNOWN;
        }

        return category;
    }
}
