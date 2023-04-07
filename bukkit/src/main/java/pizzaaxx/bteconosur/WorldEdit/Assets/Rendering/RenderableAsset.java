package pizzaaxx.bteconosur.WorldEdit.Assets.Rendering;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.WorldEdit.Assets.Asset;

import java.io.IOException;

public class RenderableAsset implements GLEventListener {

    private final BTEConoSur plugin;
    private final Asset asset;
    private RenderableModel[][][] models;

    public RenderableAsset(@NotNull BTEConoSur plugin, @NotNull Asset asset) throws IOException {
        this.plugin = plugin;
        this.asset = asset;
        this.asset.loadSchematic();
        Vector dimensions = asset.getClipboard().getDimensions();
        models = plugin.getModelsManager().getClipboard(asset.getClipboard());
    }

    private final GLU glu = new GLU();

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel( GL2.GL_SMOOTH );
        gl.glClearColor( 0f, 0f, 0f, 0f );
        gl.glClearDepth( 1.0f );
        gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(@NotNull GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
        gl.glLoadIdentity();
        gl.glTranslatef( 0f, 0f, -5.0f );

        gl.glRotatef(35.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);

        gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube
        Vector dimensions = asset.getClipboard().getDimensions();
        for (int x = 0; x < dimensions.getBlockX(); x++) {
            for (int y = 0; x < dimensions.getBlockY(); y++) {
                for (int z = 0; x < dimensions.getBlockZ(); z++) {
                    if (models[x][y][z] != null) {
                        models[x][y][z].render(gl, new double[] {x, y, z});
                    }
                }
            }
        }
        gl.glEnd();
        gl.glFlush();

    }

    @Override
    public void reshape(@NotNull GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        if( height == 0 ) height = 1;

        final float h = ( float ) width / ( float ) height;
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();

        glu.gluPerspective( 45.0f, h, 1.0, 40.0 );
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
    }
}
