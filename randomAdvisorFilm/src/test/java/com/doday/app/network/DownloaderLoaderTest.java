package com.doday.app.network;


import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DownloaderLoaderTest {

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesNotUseCache () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(30,7,2014); //Mercredi 30 juillet 2014
        Assert.assertEquals(0, maxStale);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_1_DayStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(29,7,2014); //Mardi
        Assert.assertEquals(1, maxStale/DownloaderLoader.DAY);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_2_DaysStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(28,7,2014); //Lundi
        Assert.assertEquals(2, maxStale/DownloaderLoader.DAY);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_3_DaysStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(27,7,2014); //Dimanche
        Assert.assertEquals(3, maxStale/DownloaderLoader.DAY);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_4_DaysStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(26,7,2014); //Samedi
        Assert.assertEquals(4, maxStale/DownloaderLoader.DAY);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_5_DaysStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(25,7,2014); //Vendredi
        Assert.assertEquals(5, maxStale/DownloaderLoader.DAY);
    }

    @Test
    public void getDayInWeekFromMikeKeithAlgorithme_Should_Indicate_DoesUseCache_Whith_6_DaysStale () throws Exception{
        int maxStale = DownloaderLoader.calculateMaxStale(24,7,2014); //Jeudi
        Assert.assertEquals(6, maxStale/DownloaderLoader.DAY);
    }

}

