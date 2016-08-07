package com.example.peyo.gles20;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {
    private static final String TAG = "ObjLoader";
    Context context;
    List<String> strings;
    float[] vertices;
    float[] normal;
    float[] tx;

    public ObjLoader(Context context) {
        super();
        this.context = context;
    }

    private void ObjRead(int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String string = null;
        strings = new ArrayList<String>();
        try {
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((string = bufferedReader.readLine()) != null) {
                strings.add(string);
            }
        } catch (IOException e) {
            Log.d(TAG,"Could not load obj file");
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "Couldn't close obj file");
                    e.printStackTrace();
                }
        }
    }

    public void load(int resId) {
        int vertexIndex = 0;
        int numVertices = 0;
        int normalIndex = 0;
        int numNormals = 0;
        int txIndex = 0;
        int numTx = 0;
        int fIndex = 0;
        int numFace = 0;
        int v = 0, vn = 0, vt = 0, vf = 0;
        ObjRead(resId);

        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i);
            if (string.startsWith("v ")) v++;
            if (string.startsWith("vn ")) vn++;
            if (string.startsWith("vt ")) vt++;
            if (string.startsWith("f ")) {
                String[] F = string.split("[ ]+");
                if (F.length == 5) {
                    vf++;
                    vf++;
                } else if (F.length == 4){
                    vf++;
                }
            }
        }

        float[] verticesAux = new float[v * 3];
        float[] normalsAux = new float[vn * 3];
        float[] txAux = new float[vt * 2];
        int[] f = new int[vf * 9];

        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i);
            if (string.startsWith("v ")) {
                String[] V = string.split("[ ]+");
                for (int j = 1; j <= 3; j++)
                    verticesAux[vertexIndex++] = Float.parseFloat(V[j]);
                numVertices++;
                continue;
            }
            if (string.startsWith("vn ")) {
                String[] N = string.split("[ ]+");
                for (int j = 1; j <= 3; j++)
                    normalsAux[normalIndex++] = Float.parseFloat(N[j]);
                numNormals++;
                continue;
            }
            if (string.startsWith("vt ")) {
                String[] T = string.split("[ ]+");
                for (int j = 1; j <= 2; j++)
                    txAux[txIndex++] = Float.parseFloat(T[j]);
                numTx++;
                continue;
            }
            if (string.startsWith("f ")) {
                String[] F = string.split("[ ]+");
                if (F.length == 4) {
                    for (int j = 1; j <= 3; j++) {
                        String[] V = F[j].split("/");
                        for (int k = 0; k <= 2; k++)
                            f[fIndex++] = Integer.parseInt("0"+V[k]);

                    }
                    numFace++;
                } else if(F.length == 5) {
                    for (int j = 1; j <= 3; j++) {
                        String[] V = F[j].split("/");
                        for (int k = 0; k <= 2; k++)
                            f[fIndex++] = Integer.parseInt("0"+V[k]);

                    }
                    numFace++;
                    for (int j = 2; j <= 4; j++) {
                        String[] V = F[1 + (j % 4)].split("/");
                        for (int k = 0; k <= 2; k++)
                            f[fIndex++] = Integer.parseInt("0"+V[k]);

                    }
                    numFace++;
                }
            }
        }

        vertices = new float[(f.length / 3) * 3];
        tx = new float[(f.length / 3) * 2];
        normal = new float[(f.length / 3) * 3];

        for (int n = 0, nt = 0; n < fIndex; n += 3, nt += 2) {
            for (int i = 0; i <= 2; i++)
                vertices[n + i] = verticesAux[(3 * f[n]) - 3 + i];
            for (int i = 0; i <= 1; i++)
                tx[nt + i] = vt > 0 ? txAux[(2 * f[n+1]) -2 + i] : 0;
            for (int i = 0; i <= 2 ;i++)
                normal[n +i] = normalsAux[(3 * f[n+2]) - 3 + i];
        }

    }
}
