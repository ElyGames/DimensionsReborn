package net.samagames.dimensionsv2;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.GamesNames;
import net.samagames.dimensionsv2.game.DimensionsGame;
import net.samagames.dimensionsv2.game.entity.chestitem.ChestItemManager;
import net.samagames.dimensionsv2.game.entity.dimension.DimensionsManager;
import net.samagames.dimensionsv2.game.listeners.ChestItemListener;
import net.samagames.dimensionsv2.game.listeners.DamageListener;
import net.samagames.dimensionsv2.game.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

/**
 * Created by Tigger_San on 21/04/2017.
 */
public class Dimensions extends JavaPlugin
{
    private SamaGamesAPI api;
    private DimensionsGame game;
    private static Dimensions instance;

    @Override
    public void onEnable(){
        instance = this;
        this.api = SamaGamesAPI.get();
        this.game = new DimensionsGame();
        SamaGamesAPI.get().getGameManager().registerGame(game);
        SamaGamesAPI.get().getShopsManager().setShopToLoad(GamesNames.DIMENSION, true);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        getServer().getPluginManager().registerEvents(new ChestItemListener(),this);
        getServer().getPluginManager().registerEvents(new DamageListener(),this);
        ChestItemManager.getInstance();
        DimensionsManager.getInstance();

    }



    @Override
    public void onDisable(){
        //TEMPORARY FOR LOCAL TESTS
        try {
            copyFolder(new File("dimensions"),new File("world"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static Dimensions getInstance() {
        return instance;
    }

    public SamaGamesAPI getApi() {
        return api;
    }


    public DimensionsGame getGame() {
        return game;
    }


    //TEMPORARY FOR LOCAL TEST
    public static void copyFolder(File src, File dest)
            throws IOException
    {
        if (src.isDirectory())
        {
            if (!dest.exists())
            {
                dest.mkdir();
                System.out.println("Directory copied from " + src + "  to " + dest);
            }
            String[] files = src.list();
            String[] arrayOfString1;
            int j = (arrayOfString1 = files).length;
            for (int i = 0; i < j; i++)
            {
                String file = arrayOfString1[i];
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[63];
            int length;
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }
}
