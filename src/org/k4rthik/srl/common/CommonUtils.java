package org.k4rthik.srl.common;

import java.awt.image.BufferedImage;

/**
 * Author: Karthik
 * Date  : 7/23/2014.
 */

@SuppressWarnings("unused")
public class CommonUtils
{
    public static int[][] getBinaryArray_BinaryImage(BufferedImage bufferedImage)
    {
        int[][] pixelArr = new int[bufferedImage.getHeight(null)][];

        // Get 2D array from binary iamge
        for (int y = 0; y < bufferedImage.getHeight(); y++)
        {
            pixelArr[y] = new int[bufferedImage.getWidth()];

            for (int x = 0; x < bufferedImage.getWidth(); x++)
            {
                pixelArr[y][x] = (bufferedImage.getRGB(x, y) == 0xFFFFFFFF ? 0 : 1);
            }
        }

        return pixelArr;
    }

    public static int[][] getBinaryArray_RGBImage(BufferedImage bufferedImage)
    {
        int[][] pixelMap = new int[bufferedImage.getHeight()][];

        // Get 2D array from RGB image
        for (int y = 0; y < bufferedImage.getHeight(null); y++)
        {
            pixelMap[y] = new int[bufferedImage.getWidth(null)];

            for (int x = 0; x < bufferedImage.getWidth(null); x++)
            {
                int r = (bufferedImage.getRGB(x, y) >> 16) & 0x0FF;
                int g = (bufferedImage.getRGB(x, y) >>  8) & 0x0FF;
                int b = (bufferedImage.getRGB(x, y)      ) & 0x0FF;
                pixelMap[y][x] = (0.30*r + 0.59*g + 0.11*b) > 127 ? 0 : 1;
            }
        }

        return pixelMap;
    }

    public static void print2DArray(int[][] array2D)
    {
        for (int[] array : array2D)
        {
            for (int integer : array)
            {
                System.out.print(integer);
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    public static void print2DArray(double[][] array2D)
    {
        for (double[] array : array2D)
        {
            for (double value : array)
            {
                System.out.print(value);
                System.out.print(' ');
            }
            System.out.println();
        }
    }
}