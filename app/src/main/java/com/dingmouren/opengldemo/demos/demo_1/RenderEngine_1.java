package com.dingmouren.opengldemo.demos.demo_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;

/**
 *
 */

public class RenderEngine_1 {

    private FloatBuffer mBuffer;
    private int vertexShader = -1;
    private int fragmentShader = -1;
    private int mShaderProgram = -1;

    private static final float[] vertexData = {
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    /*顶点着色器代码*/
    private static final String VERTEX_SHADER = "" +
            "attribute vec4 " + POSITION_ATTRIBUTE + ";\n" +
            "uniform mat4 " + TEXTURE_MATRIX_UNIFORM + ";\n" +
            "attribute vec4 " + TEXTURE_COORD_ATTRIBUTE + ";\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main()\n" +
            "{\n" +
            "  vTextureCoord = (" + TEXTURE_MATRIX_UNIFORM + " * " + TEXTURE_COORD_ATTRIBUTE + ").xy;\n" +
            "  gl_Position = " + POSITION_ATTRIBUTE + ";\n" +
            "}\n";

    /*片段着色器代码*/
    private static final String FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES " + TEXTURE_SAMPLER_UNIFORM + ";\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() \n" +
            "{\n" +
            "gl_FragColor = texture2D(" + TEXTURE_SAMPLER_UNIFORM + ", vTextureCoord);\n" +
            "}\n";

    public RenderEngine_1() {
        /*创建缓冲区*/
        mBuffer = createBuffer(vertexData);
        /*创建顶点着色器*/
        vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER);
        /*创建片段着色器*/
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        /*链接程序*/
        mShaderProgram = linkProgram(vertexShader, fragmentShader);
    }



    /**
     * 将float[]数组转为OpenGl 所需要的FloatBuffer 创建缓冲区  讲解  https://www.cnblogs.com/ruber/p/6857159.html
     * 缓冲区：在内存中预留指定大小的存储空间用来对输入/输出（io）的数据作临时存储，
     * ByteBuffer.allocateDirect(int capacity):是不使用JVM堆栈而是通过操作系统来创建内存块用作缓冲区，经常重用的话可以使用这个缓冲区
     * ByteBuffer.allocate(int capacity):从堆空间中分配一个容量大小为capacity的byte数组作为缓冲区的byte数据存储器
     * @param vertexData
     * @return
     */
    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)//参数是缓存容量的字节数，一个float类型占用四个字节，32位
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();//创建一个浮点缓冲区
        buffer.put(vertexData, 0, vertexData.length)//在每次调用put加入点后position都会加1
                .position(0);//设置缓冲区从0开始读取
        return buffer;
    }

    /**
     * 创建着色器对象，加载着色器代码，编译着色器
     * @param type
     * @param shaderSource
     * @return
     */
    public int loadShader(int type, String shaderSource) {
        /*根据类型创建顶点着色器还是片段着色器*/
        int shader = glCreateShader(type);
          /*创建失败时抛出异常*/
        if (shader == 0) {
            throw new RuntimeException("Create Shader Failed!" + glGetError());
        }
        /*加载着色器代码*/
        glShaderSource(shader, shaderSource);
         /*编译着色器*/
        glCompileShader(shader);
        return shader;
    }

    /**
     * 创建OpenGL ES 程序
     * 着色器的代码执行是很昂贵滴
     * @param verShader
     * @param fragShader
     */
    public int linkProgram(int verShader, int fragShader) {
         /*创建空的OpenGL ES 程序*/
        int program = glCreateProgram();
         /*创建空的OpenGL ES程序失败抛出异常*/
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + glGetError());
        }
        /*添加顶点着色器到程序中*/
        glAttachShader(program, verShader);
         /*添加片段着色器到程序中*/
        glAttachShader(program, fragShader);
        /*创建OpenGL ES程序的可执行文件*/
        glLinkProgram(program);
        /*应用这个program*/
        glUseProgram(program);
        return program;
    }

    public int getShaderProgram() {
        return mShaderProgram;
    }

    public FloatBuffer getBuffer() {
        return mBuffer;
    }
}

