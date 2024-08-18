package com.example.exergames_beta.realTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.exergames_beta.util.Coordenada;
import com.example.exergames_beta.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceDetectionAnalyzer extends AppCompatActivity implements ImageAnalysis.Analyzer {

    // CONSTANTS
    private final FaceDetector faceDetector;        // Detector de rostros
    private final float MIN_MOVEMENT_X = 25.0f;     // Sensibilidad de movimientos en X
    private final float MIN_MOVEMENT_Y = 25.0f;     // Sensibilidad de movimientos en Y

    // VARS
    private Preview preview;
    private PreviewView viewFinder;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView coordenadasTextView, desplazamientoTextView, movimientoTextView;
    private TextView desplazamientoAbsTextView, infoTextView;
    private Coordenada coordenadaActual, coordenadaAnterior;
    public float despX, despY, absoluteDistance;
    private boolean primeraVez;
    private String vMovement, hMovement;
    private GraphicOverlay graphicOverlay;
    private int numCalls;

    // LISTENER
    public OnFaceMovementListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facedetectionanalyzer);

        viewFinder = findViewById(R.id.viewFinder);
        coordenadasTextView = findViewById(R.id.coordenadasTextView);
        desplazamientoTextView = findViewById(R.id.desplazamientoTextView);
        movimientoTextView = findViewById(R.id.movimientoTextView);
        graphicOverlay = findViewById(R.id.graphicOverlay);
        desplazamientoAbsTextView = findViewById(R.id.desplazamientoAbsTextView);
        infoTextView = findViewById(R.id.infoTextView);

        primeraVez = true;

        preview = null;

        numCalls = 0;

        coordenadaActual = new Coordenada();
        coordenadaAnterior = new Coordenada();

        vMovement = "Ninguno";
        hMovement = "Ninguno";

        Context context = this;
        this.startCamera(context);
    }

    public FaceDetectionAnalyzer() {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(options);
        coordenadaActual = new Coordenada();
        coordenadaAnterior = new Coordenada();
    }

    public FaceDetectionAnalyzer(Context context) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build();

        faceDetector = FaceDetection.getClient(options);
        coordenadaActual = new Coordenada();
        coordenadaAnterior = new Coordenada();
        executor = Executors.newSingleThreadExecutor();
        startCamera(context);
    }

    public void setOnFaceMovementListener(OnFaceMovementListener listener) {
        this.listener = listener;
    }

    private void startCamera(Context context) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Configuración de la vista previa
                if (!(viewFinder==null)) {
                    preview = new Preview.Builder()
                            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                            .build();
                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                }

                // Configuración del análisis de imagen
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(executor, this);

                // Selector de camara frontal
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                // Desvincula y vuelve a vincular los casos de uso
                cameraProvider.unbindAll();
                if (!(viewFinder==null)) {
                    cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageAnalysis);
                } else {
                    cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageAnalysis);
                }

            } catch (ExecutionException | InterruptedException e) {
                // Manejo de excepciones
                Log.e("CameraXApp", "Error al iniciar la cámara", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @Override
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void analyze(@NonNull ImageProxy image) {
        numCalls++;
        Image mediaImage = image.getImage();

        if (mediaImage != null) {
            int rotationDegrees = image.getImageInfo().getRotationDegrees();
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees);
            //InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

            faceDetector.process(inputImage)
                    .addOnSuccessListener(faces -> {
                        runOnUiThread(() -> {
                            // Comprobar si hay alguna cara detectada en la lista de caras
                            if (!faces.isEmpty()) {
                                // Solo interesa la primera cara encontrada
                                Face face = faces.get(0);
                                FaceLandmark noseBase = face.getLandmark(FaceLandmark.NOSE_BASE);
                                if (noseBase != null) {
                                    // OBTENER COORDENADAS
                                    PointF noseBasePosition = noseBase.getPosition();

                                    // Ajustar coordenadas para mirroring horizontal
                                    float adjustedX;
                                    if (viewFinder != null) {
                                        // Invertir para solucionar mirroring en vista de "Proabr Tracker"
                                        adjustedX = viewFinder.getWidth() - noseBasePosition.x;
                                    } else {
                                        // Uso de coordenada sin invertir para jugar a juegos
                                        adjustedX = noseBasePosition.x;
                                    }
                                    float adjustedY = noseBasePosition.y;

                                    coordenadaActual = new Coordenada();
                                    coordenadaActual.setX(adjustedX);
                                    coordenadaActual.setY(adjustedY);

                                    // OBTENER DESPLAZAMIENTOS Y DISTANCIAS
                                    if (primeraVez) {
                                        primeraVez = false;
                                        despX = 0;
                                        despY = 0;
                                        absoluteDistance = 0;
                                    } else {
                                        despX = coordenadaActual.getX() - coordenadaAnterior.getX();
                                        despY = coordenadaActual.getY() - coordenadaAnterior.getY();
                                        absoluteDistance = coordenadaActual.getAbsoluteDistance(coordenadaAnterior);
                                    }

                                    // DETECTAR MOVIMIENTOS EN BASE A VARIACIONES DE COORDENADAS
                                    hMovement = this.getHorizontalFaceMovement(despX);
                                    vMovement = this.getVerticalFaceMovement(despY);

                                    // LISTENER QUE LLAMA A LA ACCIÓN EN JUEGOS
                                    if (listener != null) {
                                        listener.onFaceMovement(hMovement, vMovement);
                                    }

                                    String text = "";
                                    if (!(viewFinder==null)) {
                                        Log.i("ANCHO PREVIEW",""+viewFinder.getWidth());
                                        Log.i("ALTO PREVIEW",""+viewFinder.getHeight());
                                        Log.i("ANCHO IMAGEN ANALIZADA",""+inputImage.getWidth());
                                        Log.i("ALTO IMAGEN ANALIZADA",""+inputImage.getHeight());

                                        // PINTAR GRAPHIC OVERLAY
                                        graphicOverlay.updateCoordinates(
                                                coordenadaActual.getX(),        // X nose
                                                coordenadaActual.getY(),        // Y nose
                                                viewFinder.getWidth(),          // preview width
                                                viewFinder.getHeight(),         // preview height
                                                inputImage.getWidth(),          // analyzed image width
                                                inputImage.getHeight(),         // analyzed image height
                                                rotationDegrees                 // rotation degrees
                                        );

                                        // PRINT DEL MOVIMIENTO DETECTADO
                                        // Coordenadas
                                        text = "Coordenadas: ("+coordenadaActual.getX()+","+coordenadaActual.getY()+")";
                                        coordenadasTextView.setText(text);
                                        // Desplazamiento (actualPosition - previousPosition)
                                        text = "Desplazamiento en X e Y: ("+despX+","+despY+")";
                                        desplazamientoTextView.setText(text);
                                        // Movimientos detectados (arriba, abajo, izq, dch)
                                        text = "Movimiento detectado: ";
                                        if (hMovement.isEmpty() && vMovement.isEmpty()) {
                                            text += "Ninguno";
                                        }
                                        if (!hMovement.isEmpty()) {
                                            text += hMovement;
                                        }
                                        if (!vMovement.isEmpty()) {
                                            if(hMovement.isEmpty()) {
                                                text += vMovement;
                                            } else {
                                                text += " y "+vMovement;
                                            }
                                        }
                                        movimientoTextView.setText(text);
                                        // Desplazamiento (formula distancia euclidiana)
                                        text = "Desplazamiento absoluto: "+absoluteDistance;
                                        desplazamientoAbsTextView.setText(text);
                                        // Datos del programa para conocer el estado de la ejecucion
                                        text="Análisis realizados: "+numCalls+
                                                ", Distancia entre coordenadas: "+absoluteDistance+
                                                "\nCoordenada anterior: "+coordenadaAnterior.toString();
                                        infoTextView.setText(text);
                                    }


                                    // Mensaje en el Logcat para debugging
                                    Log.i("INFO COORDENADAS", text);

                                    // PARA EL PROXIMO ANALISIS
                                    coordenadaAnterior = coordenadaActual;
                                    vMovement = "Ninguno";
                                    hMovement = "Ninguno";
                                } else {
                                    if (!(viewFinder==null)) {
                                        coordenadasTextView.setText("No se ha podido encontrar una nariz en el rostro");
                                        desplazamientoTextView.setText("No se ha podido encontrar un desplazamiento");
                                        movimientoTextView.setText("No se ha podido detectar un movimiento");
                                        Log.i("INFO COORDENADAS", "Cara detectada pero nariz no detectada");
                                    }
                                }
                            } else {
                                Log.i("INFO COORDENADAS", "NO SE HAN DETECTADO CARAS");
                                if (!(viewFinder==null)) {
                                    coordenadasTextView.setText("No se han detectado caras");
                                    desplazamientoTextView.setText("");
                                    movimientoTextView.setText("");
                                    desplazamientoAbsTextView.setText("");
                                    infoTextView.setText("");
                                }
                            }
                        });
                    })
                    .addOnFailureListener(faces -> {
                        runOnUiThread(() -> {
                            Log.i("INFO COORDENADAS", "NO SE HA PODIDO DETECTAR COORDENADAS EN UNA CARA O HA HABIDO UN ERROR");
                            if (!(viewFinder==null)) {
                                coordenadasTextView.setText("Error al detectar rostros");
                                desplazamientoTextView.setText("");
                                movimientoTextView.setText("");
                                desplazamientoAbsTextView.setText("");
                                infoTextView.setText("");
                            }
                        });
                    })
                    .addOnCompleteListener(task -> image.close());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // Obtener movimiento horizontal en base a coordenadas
    private String getHorizontalFaceMovement(float displacementX) {
        // Sensibilidad de 20 puntos en el eje X
        if (Math.abs(displacementX) > MIN_MOVEMENT_X) {
            if (displacementX < 0) {
                return "Derecha";
            } else if (displacementX > 0) {
                return "Izquierda";
            }
        }
        return "";
    }

    // Obtener movimiento vertical en base a coordenadas
    private String getVerticalFaceMovement(float displacementY) {
        // Sensibilidad de 20 puntos en el eje Y
        if (Math.abs(displacementY) > MIN_MOVEMENT_Y) {
            if (displacementY < 0) {
                return "Arriba";
            } else if (displacementY > 0) {
                return "Abajo";
            }
        }
        return "";
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}

