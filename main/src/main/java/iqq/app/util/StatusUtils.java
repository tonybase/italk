package iqq.app.util;

import com.alee.utils.ImageUtils;
import iqq.api.bean.IMStatus;
import iqq.api.bean.IMUser;
import iqq.app.core.context.IMContext;
import iqq.app.core.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tony on 5/20/15.
 */
public class StatusUtils {
    private static final Logger LOG = LoggerFactory.getLogger(StatusUtils.class);
    public static final Map<String, BufferedImage> statusCache = new HashMap<String, BufferedImage>();

    /**
     * 效率有问题，待优化
     * <p/>
     * 性能优化：
     * 因为加载用户时，第一时间用户是没有下载头像的
     * 而每个用户都会调用到了默认的头像和默认头像的状态
     * 我把默认头像的状态都放入到mapCache中，加快加载和画的时间
     * 最后，打开群的成员下载也卡，用起来也爽YY了
     * 如果有朋友有什么优化建议的一定要说啊
     */
    public static BufferedImage drawStatusFace(IMUser user) {
        BufferedImage face = user.getAvatarBuffered();
        BufferedImage def_face = statusCache.get("defaultFace");
        if (face == null) {
            face = def_face;
            if (face == null) {
                face = UIUtils.getDefaultAvatarBuffer();
                statusCache.put("def_face", face);
            }
        }

        IMStatus status = user.getStatus();
        if (status == null) {
            status = IMStatus.OFFLINE;
        }

        BufferedImage canvas = null;
        if (status == IMStatus.OFFLINE) {
            // 看看是不是系统默认头像
            if (face == def_face) {
                canvas = statusCache.get("defaultFace_gray");
                if (canvas == null) {
                    canvas = createGrayscaleCopy(face);
                    statusCache.put("defaultFace_gray", canvas);
                }
            } else {
                canvas = createGrayscaleCopy(face);
            }
            return canvas;
        }

        // 状态图标加入缓存中，加快频繁更新状态的响应
        BufferedImage statusIcon = cacheStatusIcon(status.name);

        // 看看是不是系统默认头像
        if (face == def_face) {
            canvas = cacheCanvas(face, statusIcon, "def_" + status.name);
        } else {
            canvas = drawStatusFace(face, statusIcon);
        }

        return canvas;
    }

    public static BufferedImage createGrayscaleCopy(BufferedImage sourceImg) {
        // return ImageUtils.createGrayscaleCopy(face);
        BufferedImage image = new BufferedImage(sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, sourceImg.getWidth(), sourceImg.getHeight());
        g.drawImage(sourceImg, 0, 0, null);
        g.dispose();
        return image;
    }

    public static BufferedImage cacheCanvas(BufferedImage face, BufferedImage statusIcon, String canvasKey) {
        BufferedImage canvas = statusCache.get(canvasKey);
        if (canvas == null) {
            canvas = drawStatusFace(face, statusIcon);
            statusCache.put(canvasKey, canvas);
        }
        return canvas;
    }

    public static BufferedImage drawStatusFace(BufferedImage face, BufferedImage stat) {
        BufferedImage canvas = ImageUtils.createCompatibleImage(face);
        Graphics2D g2d = canvas.createGraphics();
        g2d.drawImage(face, 0, 0, null);
        g2d.drawImage(stat, canvas.getWidth() - stat.getWidth(), canvas.getHeight() - stat.getHeight(), null);
        g2d.dispose();
        return canvas;
    }


    public static BufferedImage cacheStatusIcon(String status) {
        BufferedImage statusIcon = statusCache.get(status);
        if (statusIcon == null) {
            statusIcon = ImageUtils.getBufferedImage(IMContext.getBean(ResourceService.class).getFile("icons/status/" + status + ".png").getAbsolutePath());
            statusIcon = ImageUtils.getBufferedImage(statusIcon.getScaledInstance(14, 14, 100));
            statusCache.put(status, statusIcon);
        }
        return statusIcon;
    }

}
