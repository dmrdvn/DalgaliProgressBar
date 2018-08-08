package me.itangqi.waveloadingview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import me.itangqi.library.R;

public class WaveLoadingView extends View {
    /**
     * +------------------------+
     * | wave length - 波长      |__________
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude - 振幅
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|_______
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level - 水位
     * |                        |  |
     * |                        |  |
     * +------------------------+__|_______
     */
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.1f;
    private static final float DEFAULT_AMPLITUDE_VALUE = 50.0f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;
    private static final int DEFAULT_WAVE_PROGRESS_VALUE = 50;
    private static final int DEFAULT_WAVE_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_WAVE_BACKGROUND_COLOR = Color.parseColor("#00000000");
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#212121");
    private static final int DEFAULT_STROKE_COLOR = Color.TRANSPARENT;
    private static final float DEFAULT_BORDER_WIDTH = 0;
    private static final float DEFAULT_TITLE_STROKE_WIDTH = 0;
    // This is incorrect/not recommended by Joshua Bloch in his book Effective Java (2nd ed).
    private static final int DEFAULT_WAVE_SHAPE = ShapeType.CIRCLE.ordinal();
    private static final int DEFAULT_TRIANGLE_DIRECTION = TriangleDirection.NORTH.ordinal();
    private static final int DEFAULT_ROUND_RECTANGLE_X_AND_Y = 30;
    private static final float DEFAULT_TITLE_TOP_SIZE = 18.0f;
    private static final float DEFAULT_TITLE_CENTER_SIZE = 22.0f;
    private static final float DEFAULT_TITLE_BOTTOM_SIZE = 18.0f;

    public enum ShapeType {
        TRIANGLE,
        CIRCLE,
        SQUARE,
        RECTANGLE
    }

    public enum TriangleDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    // Dinamik Özellikler.
    private int mCanvasSize;
    private int mCanvasHeight;
    private int mCanvasWidth;
    private float mAmplitudeRatio;
    private int mWaveBgColor;
    private int mWaveColor;
    private int mShapeType;
    private int mTriangleDirection;
    private int mRoundRectangleXY;

    // Özellikler.
    private String mTopTitle;
    private String mCenterTitle;
    private String mBottomTitle;
    private float mDefaultWaterLevel;
    private float mWaterLevelRatio = 1f;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;
    private int mProgressValue = DEFAULT_WAVE_PROGRESS_VALUE;
    private boolean mIsRoundRectangle;

    // Nesne çizmek için kullanılır.
    // Tekrarlanan dalgalar için gölgelendirici.
    private BitmapShader mWaveShader;
    private Bitmap bitmapBuffer;
    // Gölgelendirme matrisi.
    private Matrix mShaderMatrix;
    // Dalga çizme noktası.
    private Paint mWavePaint;
    //Dalga arka planı çizme noktası.
    private Paint mWaveBgPaint;
    // Sınırı çizme noktası.
    private Paint mBorderPaint;
    // Başlığı çizme noktası.
    private Paint mTopTitlePaint;
    private Paint mBottomTitlePaint;
    private Paint mCenterTitlePaint;

    private Paint mTopTitleStrokePaint;
    private Paint mBottomTitleStrokePaint;
    private Paint mCenterTitleStrokePaint;

    // Animasyon.
    private ObjectAnimator waveShiftAnim;
    private AnimatorSet mAnimatorSet;

    private Context mContext;

    // Yapıcı & Init Methodu.
    public WaveLoadingView(final Context context) {
        this(context, null);
    }

    public WaveLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        // Dalgayı ilkledik.
        mShaderMatrix = new Matrix();
        mWavePaint = new Paint();
        //ANTI_ALIAS_FLAG, çizimin kenarlarını yumuşatır.
        // fakat şeklin iç kısmı üzerinde hiçbir etkisi yoktur.Sadece dış çeperi yumuşatır.
        mWavePaint.setAntiAlias(true);
        mWaveBgPaint = new Paint();
        mWaveBgPaint.setAntiAlias(true);
        // Animasyonları ilkledik.
        initAnimation();

        //Stil özelliklerini yükledik ve set ettik.
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.WaveLoadingView, defStyleAttr, 0);

        // Şekil Türünü İlkledik.
        mShapeType = attributes.getInteger(R.styleable.WaveLoadingView_wlv_shapeType, DEFAULT_WAVE_SHAPE);

        // Dalgayı İlkledik.
        mWaveColor = attributes.getColor(R.styleable.WaveLoadingView_wlv_waveColor, DEFAULT_WAVE_COLOR);
        mWaveBgColor = attributes.getColor(R.styleable.WaveLoadingView_wlv_wave_background_Color, DEFAULT_WAVE_BACKGROUND_COLOR);

        mWaveBgPaint.setColor(mWaveBgColor);

        // Dalga Sıklık Oranını İlkledik.
        float amplitudeRatioAttr = attributes.getFloat(R.styleable.WaveLoadingView_wlv_waveAmplitude, DEFAULT_AMPLITUDE_VALUE) / 1000;
        mAmplitudeRatio = (amplitudeRatioAttr > DEFAULT_AMPLITUDE_RATIO) ? DEFAULT_AMPLITUDE_RATIO : amplitudeRatioAttr;

        // Progress'i İlkledik.
        mProgressValue = attributes.getInteger(R.styleable.WaveLoadingView_wlv_progressValue, DEFAULT_WAVE_PROGRESS_VALUE);
        setProgressValue(mProgressValue);

        // Yuvarlak Dikdörtgeni İlkledik.
        mIsRoundRectangle = attributes.getBoolean(R.styleable.WaveLoadingView_wlv_round_rectangle, false);
        mRoundRectangleXY = attributes.getInteger(R.styleable.WaveLoadingView_wlv_round_rectangle_x_and_y, DEFAULT_ROUND_RECTANGLE_X_AND_Y);

        // Üçgenin Yönünü İlkledik.
        mTriangleDirection = attributes.getInteger(R.styleable.WaveLoadingView_wlv_triangle_direction, DEFAULT_TRIANGLE_DIRECTION);

        // Kenarlığı İlkledik.
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_borderWidth, dp2px(DEFAULT_BORDER_WIDTH)));
        mBorderPaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_borderColor, DEFAULT_WAVE_COLOR));

        // Üst Başlığı İlkledik.
        mTopTitlePaint = new Paint();
        mTopTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleTopColor, DEFAULT_TITLE_COLOR));
        mTopTitlePaint.setStyle(Paint.Style.FILL);
        mTopTitlePaint.setAntiAlias(true);
        mTopTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleTopSize, sp2px(DEFAULT_TITLE_TOP_SIZE)));

        mTopTitleStrokePaint = new Paint();
        mTopTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleTopStrokeColor, DEFAULT_STROKE_COLOR));
        mTopTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleTopStrokeWidth, dp2px(DEFAULT_TITLE_STROKE_WIDTH)));
        mTopTitleStrokePaint.setStyle(Paint.Style.STROKE);
        mTopTitleStrokePaint.setAntiAlias(true);
        mTopTitleStrokePaint.setTextSize(mTopTitlePaint.getTextSize());

        mTopTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleTop);

        // Init Center Title
        mCenterTitlePaint = new Paint();
        mCenterTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleCenterColor, DEFAULT_TITLE_COLOR));
        mCenterTitlePaint.setStyle(Paint.Style.FILL);
        mCenterTitlePaint.setAntiAlias(true);
        mCenterTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleCenterSize, sp2px(DEFAULT_TITLE_CENTER_SIZE)));

        mCenterTitleStrokePaint = new Paint();
        mCenterTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleCenterStrokeColor, DEFAULT_STROKE_COLOR));
        mCenterTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleCenterStrokeWidth, dp2px(DEFAULT_TITLE_STROKE_WIDTH)));
        mCenterTitleStrokePaint.setStyle(Paint.Style.STROKE);
        mCenterTitleStrokePaint.setAntiAlias(true);
        mCenterTitleStrokePaint.setTextSize(mCenterTitlePaint.getTextSize());

        mCenterTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleCenter);

        // Init Bottom Title
        mBottomTitlePaint = new Paint();
        mBottomTitlePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleBottomColor, DEFAULT_TITLE_COLOR));
        mBottomTitlePaint.setStyle(Paint.Style.FILL);
        mBottomTitlePaint.setAntiAlias(true);
        mBottomTitlePaint.setTextSize(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleBottomSize, sp2px(DEFAULT_TITLE_BOTTOM_SIZE)));

        mBottomTitleStrokePaint = new Paint();
        mBottomTitleStrokePaint.setColor(attributes.getColor(R.styleable.WaveLoadingView_wlv_titleBottomStrokeColor, DEFAULT_STROKE_COLOR));
        mBottomTitleStrokePaint.setStrokeWidth(attributes.getDimension(R.styleable.WaveLoadingView_wlv_titleBottomStrokeWidth, dp2px(DEFAULT_TITLE_STROKE_WIDTH)));
        mBottomTitleStrokePaint.setStyle(Paint.Style.STROKE);
        mBottomTitleStrokePaint.setAntiAlias(true);
        mBottomTitleStrokePaint.setTextSize(mBottomTitlePaint.getTextSize());

        mBottomTitle = attributes.getString(R.styleable.WaveLoadingView_wlv_titleBottom);

        attributes.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        mCanvasSize = canvas.getWidth();
        if (canvas.getHeight() < mCanvasSize) {
            mCanvasSize = canvas.getHeight();
        }
        // Draw Wave.
        // Modify paint shader according to mShowWave state.
        if (mWaveShader != null) {
            // First call after mShowWave, assign it to our paint.
            if (mWavePaint.getShader() == null) {
                mWavePaint.setShader(mWaveShader);
            }

            // Dalga uzunluğuna ve genlik oranına göre ölçeklendiriciyi ölçeklendirin.
            // TDalgaların boyutlarını belirler (genişlik için waveLengthRatio , yükseklik için amplitudeRatio ).
            mShaderMatrix.setScale(1, mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO, 0, mDefaultWaterLevel);
            // Shader'ı waveShiftRatio ve waterLevelRatio'ya göre çevir.
            // Bu, başlangıç konumlarına (x için waveShiftRatio, y için waterLevelRatio) karar verir.
            mShaderMatrix.postTranslate(mWaveShiftRatio * getWidth(),
                    (DEFAULT_WATER_LEVEL_RATIO - mWaterLevelRatio) * getHeight());

            // Gölgelendiriciyi geçersiz kılmak için matris atayın.
            mWaveShader.setLocalMatrix(mShaderMatrix);

            // BorderWidth'i al.
            float borderWidth = mBorderPaint.getStrokeWidth();

            // Varsayılan tip üçgendir.
            switch (mShapeType) {
                // Draw triangle
                case 0:
                    // Şu anda sınır ayarlarını desteklemiyor
                    Point start = new Point(0, getHeight());
                    Path triangle = getEquilateralTriangle(start, getWidth(), getHeight(), mTriangleDirection);
                    canvas.drawPath(triangle, mWaveBgPaint);
                    canvas.drawPath(triangle, mWavePaint);
                    break;
                // Daire çiz
                case 1:
                    if (borderWidth > 0) {
                        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f,
                                (getWidth() - borderWidth) / 2f - 1f, mBorderPaint);
                    }

                    float radius = getWidth() / 2f - borderWidth;
                    // Arkaplanı çiz
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mWaveBgPaint);
                    canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mWavePaint);
                    break;
                // Kare çiz
                case 2:
                    if (borderWidth > 0) {
                        canvas.drawRect(
                                borderWidth / 2f,
                                borderWidth / 2f,
                                getWidth() - borderWidth / 2f - 0.5f,
                                getHeight() - borderWidth / 2f - 0.5f,
                                mBorderPaint);
                    }

                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                            getHeight() - borderWidth, mWaveBgPaint);
                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                            getHeight() - borderWidth, mWavePaint);
                    break;
                // Dikdörtgen çiz
                case 3:
                    if (mIsRoundRectangle) {
                        if (borderWidth > 0) {
                            RectF rect = new RectF(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mBorderPaint);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                        } else {
                            RectF rect = new RectF(0, 0, getWidth(), getHeight());
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWaveBgPaint);
                            canvas.drawRoundRect(rect, mRoundRectangleXY, mRoundRectangleXY, mWavePaint);
                        }
                    } else {
                        if (borderWidth > 0) {
                            canvas.drawRect(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f, mWaveBgPaint);
                            canvas.drawRect(borderWidth / 2f, borderWidth / 2f, getWidth() - borderWidth / 2f - 0.5f, getHeight() - borderWidth / 2f - 0.5f, mWavePaint);
                        } else {
                            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mWaveBgPaint);
                            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mWavePaint);
                        }
                    }
                    break;
                default:
                    break;
            }

            // I know, the code written here is very shit.
            if (!TextUtils.isEmpty(mTopTitle)) {
                float top = mTopTitlePaint.measureText(mTopTitle);
                // Üst metnin vuruşunu çiz
                canvas.drawText(mTopTitle, (getWidth() - top) / 2,
                        getHeight() * 2 / 10.0f, mTopTitleStrokePaint);
                // Üst Metni çiz
                canvas.drawText(mTopTitle, (getWidth() - top) / 2,
                        getHeight() * 2 / 10.0f, mTopTitlePaint);
            }

            if (!TextUtils.isEmpty(mCenterTitle)) {
                float middle = mCenterTitlePaint.measureText(mCenterTitle);
                // Orta Metnin kontorunu çiz
                canvas.drawText(mCenterTitle, (getWidth() - middle) / 2,
                        getHeight() / 2 - ((mCenterTitleStrokePaint.descent() + mCenterTitleStrokePaint.ascent()) / 2), mCenterTitleStrokePaint);
                // Ortalanmış metni çiz
                canvas.drawText(mCenterTitle, (getWidth() - middle) / 2,
                        getHeight() / 2 - ((mCenterTitlePaint.descent() + mCenterTitlePaint.ascent()) / 2), mCenterTitlePaint);
            }

            if (!TextUtils.isEmpty(mBottomTitle)) {
                float bottom = mBottomTitlePaint.measureText(mBottomTitle);
                // Alt metnin kontorunu çiz
                canvas.drawText(mBottomTitle, (getWidth() - bottom) / 2,
                        getHeight() * 8 / 10.0f - ((mBottomTitleStrokePaint.descent() + mBottomTitleStrokePaint.ascent()) / 2), mBottomTitleStrokePaint);
                // Alt metni çiz
                canvas.drawText(mBottomTitle, (getWidth() - bottom) / 2,
                        getHeight() * 8 / 10.0f - ((mBottomTitlePaint.descent() + mBottomTitlePaint.ascent()) / 2), mBottomTitlePaint);
            }
        } else {
            mWavePaint.setShader(null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // ShapType dikdörtgen ise;
        if (getShapeType() == 3) {
            mCanvasWidth = w;
            mCanvasHeight = h;
        } else {
            mCanvasSize = w;
            if (h < mCanvasSize)
                mCanvasSize = h;
        }
        updateWaveShader();
    }

    private void updateWaveShader() {
        // IllegalArgumentException: View'dan Bitmap'i yüklerken genişlik ve yükseklik > 0 olmalıdır
        // http://stackoverflow.com/questions/17605662/illegalargumentexception-width-and-height-must-be-0-while-loading-bitmap-from
        if (bitmapBuffer == null || haveBoundsChanged()) {
            if (bitmapBuffer != null)
                bitmapBuffer.recycle();
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            if (width > 0 && height > 0) {
                double defaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width;
                float defaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO;
                mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO;
                float defaultWaveLength = width;

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);

                Paint wavePaint = new Paint();
                wavePaint.setStrokeWidth(2);
                wavePaint.setAntiAlias(true);

                // Varsayılan dalgaları bitmap'e çizin.
                // y=Asin(ωx+φ)+h
                final int endX = width + 1;
                final int endY = height + 1;

                float[] waveY = new float[endX];

                wavePaint.setColor(adjustAlpha(mWaveColor, 0.3f));
                for (int beginX = 0; beginX < endX; beginX++) {
                    double wx = beginX * defaultAngularFrequency;
                    float beginY = (float) (mDefaultWaterLevel + defaultAmplitude * Math.sin(wx));
                    canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);
                    waveY[beginX] = beginY;
                }

                wavePaint.setColor(mWaveColor);
                final int wave2Shift = (int) (defaultWaveLength / 4);
                for (int beginX = 0; beginX < endX; beginX++) {
                    canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
                }

                // Gölgelendiriciyi oluşturmak için bitmap'i kullanın.
                mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
                this.mWavePaint.setShader(mWaveShader);
            }
        }
    }

    private boolean haveBoundsChanged() {
        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
                getMeasuredHeight() != bitmapBuffer.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        // ShapType dikdörtgen ise;
        if (getShapeType() == 3) {
            setMeasuredDimension(width, height);
        } else {
            int imageSize = (width < height) ? width : height;
            setMeasuredDimension(imageSize, imageSize);
        }

    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // Ebeveyn, child değişken için kesin bir boyut belirledi.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // Child değişken belirtilen boyuta kadar istediği kadar geniş olabilir.
            result = specSize;
        } else {
            // Ebeveyn child değişken üzerinde herhangi bir kısıtlama getirmemiştir.
            result = mCanvasWidth;
        }
        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // Ne kadar büyük olduğumuz söylendi.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // Child değişken belirtilen boyuta kadar büyük olabilir.
            result = specSize;
        } else {
            // Metni ölçün (dikkat: yükseliş negatif bir sayıdır).
            result = mCanvasHeight;
        }
        return (result + 2);
    }


    public void setWaveBgColor(int color) {
        this.mWaveBgColor = color;
        mWaveBgPaint.setColor(this.mWaveBgColor);
        updateWaveShader();
        invalidate();
    }

    public int getWaveBgColor() {
        return mWaveBgColor;
    }

    public void setWaveColor(int color) {
        mWaveColor = color;
        // Renk değiştiğinde gölgelendiriciyi yeniden oluşturmanız mı gerekiyor?
//        mWaveShader = null; yapmalısınız
        updateWaveShader();
        invalidate();
    }

    public int getWaveColor() {
        return mWaveColor;
    }

    public void setBorderWidth(float width) {
        mBorderPaint.setStrokeWidth(width);
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderPaint.getStrokeWidth();
    }

    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
        updateWaveShader();
        invalidate();
    }

    public int getBorderColor() {
        return mBorderPaint.getColor();
    }

    public void setShapeType(ShapeType shapeType) {
        mShapeType = shapeType.ordinal();
        invalidate();
    }

    public int getShapeType() {
        return mShapeType;
    }

    /**
     * AmplitudeRatio'ya göre dalganın dikey boyutunu ayarlayın.
     *
     * @param amplitudeRatio Varsayılan 0,05 olacak. amplitudeRatio + waterLevelRatio sonucu 1'den az olmalıdır.
     */
    public void setAmplitudeRatio(int amplitudeRatio) {
        if (this.mAmplitudeRatio != (float) amplitudeRatio / 1000) {
            this.mAmplitudeRatio = (float) amplitudeRatio / 1000;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * Su seviyesi 0'dan WaveView değerine yükseliyor.
     *
     * @param progress Varsayılan olarak 50.
     */
    public void setProgressValue(int progress) {
        mProgressValue = progress;
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(this, "waterLevelRatio", mWaterLevelRatio, ((float) mProgressValue / 100));
        waterLevelAnim.setDuration(1000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        AnimatorSet animatorSetProgress = new AnimatorSet();
        animatorSetProgress.play(waterLevelAnim);
        animatorSetProgress.start();
    }

    public int getProgressValue() {
        return mProgressValue;
    }

    public void setWaveShiftRatio(float waveShiftRatio) {
        if (this.mWaveShiftRatio != waveShiftRatio) {
            this.mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    public void setWaterLevelRatio(float waterLevelRatio) {
        if (this.mWaterLevelRatio != waterLevelRatio) {
            this.mWaterLevelRatio = waterLevelRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    /**
     * Başlık WaveView içerisinde ayarlanıyor.
     *
     * @param topTitle Varsayılan değer null.
     */
    public void setTopTitle(String topTitle) {
        mTopTitle = topTitle;
    }

    public String getTopTitle() {
        return mTopTitle;
    }

    public void setCenterTitle(String centerTitle) {
        mCenterTitle = centerTitle;
    }

    public String getCenterTitle() {
        return mCenterTitle;
    }

    public void setBottomTitle(String bottomTitle) {
        mBottomTitle = bottomTitle;
    }

    public String getBottomTitle() {
        return mBottomTitle;
    }

    public void setTopTitleColor(int topTitleColor) {
        mTopTitlePaint.setColor(topTitleColor);
    }

    public int getTopTitleColor() {
        return mTopTitlePaint.getColor();
    }

    public void setCenterTitleColor(int centerTitleColor) {
        mCenterTitlePaint.setColor(centerTitleColor);
    }

    public int getCenterTitleColor() {
        return mCenterTitlePaint.getColor();
    }

    public void setBottomTitleColor(int bottomTitleColor) {
        mBottomTitlePaint.setColor(bottomTitleColor);
    }

    public int getBottomTitleColor() {
        return mBottomTitlePaint.getColor();
    }

    public void setTopTitleSize(float topTitleSize) {
        mTopTitlePaint.setTextSize(sp2px(topTitleSize));
    }

    public float getsetTopTitleSize() {
        return mTopTitlePaint.getTextSize();
    }

    public void setCenterTitleSize(float centerTitleSize) {
        mCenterTitlePaint.setTextSize(sp2px(centerTitleSize));
    }

    public float getCenterTitleSize() {
        return mCenterTitlePaint.getTextSize();
    }

    public void setBottomTitleSize(float bottomTitleSize) {
        mBottomTitlePaint.setTextSize(sp2px(bottomTitleSize));
    }

    public float getBottomTitleSize() {
        return mBottomTitlePaint.getTextSize();
    }

    public void setTopTitleStrokeWidth(float topTitleStrokeWidth) {
        mTopTitleStrokePaint.setStrokeWidth(dp2px(topTitleStrokeWidth));
    }

    public void setTopTitleStrokeColor(int topTitleStrokeColor) {
        mTopTitleStrokePaint.setColor(topTitleStrokeColor);
    }

    public void setBottomTitleStrokeWidth(float bottomTitleStrokeWidth) {
        mBottomTitleStrokePaint.setStrokeWidth(dp2px(bottomTitleStrokeWidth));
    }

    public void setBottomTitleStrokeColor(int bottomTitleStrokeColor) {
        mBottomTitleStrokePaint.setColor(bottomTitleStrokeColor);
    }

    public void setCenterTitleStrokeWidth(float centerTitleStrokeWidth) {
        mCenterTitleStrokePaint.setStrokeWidth(dp2px(centerTitleStrokeWidth));
    }

    public void setCenterTitleStrokeColor(int centerTitleStrokeColor) {
        mCenterTitleStrokePaint.setColor(centerTitleStrokeColor);
    }

    public void startAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.start();
        }
    }

    public void endAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    public void cancelAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public void pauseAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAnimatorSet != null) {
                mAnimatorSet.pause();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public void resumeAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAnimatorSet != null) {
                mAnimatorSet.resume();
            }
        }
    }

    /**
     * Animasyonun uzunluğunu ayarlar. Varsayılan süre 1000 milisaniyedir.
     *
     * @param duration Milisaniye cinsinden animasyonun uzunluğu.
     */
    public void setAnimDuration(long duration) {
        waveShiftAnim.setDuration(duration);
    }

    private void initAnimation() {
        // Wave waves infinitely.
        waveShiftAnim = ObjectAnimator.ofFloat(this, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(waveShiftAnim);
    }

    @Override
    protected void onAttachedToWindow() {
        startAnimation();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }

    /**
     * Faktör tarafından verilen renk transparan
     * Faktör sıfıra ne kadar yaklaşırsa saydamlık o kadar artar.
     *
     * @param color  Transparan rengi
     * @param factor 1.0f to 0.0f
     * @return int - A transplanted color
     */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Paint.setTextSize(float textSize) default unit is px.
     *
     * @param spValue Metnin gerçek boyutu
     * @return int - A transplanted sp
     */
    private int sp2px(float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Eşkenar Üçgen Çiz
     *
     * @param p1        Başlangıç noktası
     * @param width     Üçgen genişliği
     * @param height    Üçgenin yüksekliği
     * @param direction Üçgenin yönü
     * @return Path
     */
    private Path getEquilateralTriangle(Point p1, int width, int height, int direction) {
        Point p2 = null, p3 = null;
        // KUZEY
        if (direction == 0) {
            p2 = new Point(p1.x + width, p1.y);
            p3 = new Point(p1.x + (width / 2), (int) (height - Math.sqrt(3.0) / 2 * height));
        }
        // GÜNEY
        else if (direction == 1) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point(p1.x + width, p1.y - height);
            p1.x = p1.x + (width / 2);
            p1.y = (int) (Math.sqrt(3.0) / 2 * height);
        }
        // DOĞU
        else if (direction == 2) {
            p2 = new Point(p1.x, p1.y - height);
            p3 = new Point((int) (Math.sqrt(3.0) / 2 * width), p1.y / 2);
        }
        // BATI
        else if (direction == 3) {
            p2 = new Point(p1.x + width, p1.y - height);
            p3 = new Point(p1.x + width, p1.y);
            p1.x = (int) (width - Math.sqrt(3.0) / 2 * width);
            p1.y = p1.y / 2;
        }

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }
}
