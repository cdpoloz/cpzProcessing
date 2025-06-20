/*
 * Copyright 2025 Carlos Polo Zamora - CPZ - CePeZeta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cpz.processing.Bean;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.constrain;

/**
 * @author CPZ
 */
public class Fluido {

    private final PApplet sketch;
    private PImage img;
    private int cantidadMovilesMax;
    private final List<Movil> lstMoviles;
    private String modo;
    private float velNoiseMin, velNoiseMax, dVelNoise, velNoise;
    private float diametroMin, diametroMax, diametro;
    private float desviacionMax;
    private float alfaMin, alfaMax;
    private int periodo;
    private int dIndPosMin, dIndPosMax;
    private float alfaFondoMin, alfaFondoMax, alfaFondo;
    private int colorRelleno, colorOn, colorOff, colorFondo;
    private boolean running;
    private List<PVector> posiciones, normales;

    public Fluido(PApplet sketch) {
        this.sketch = sketch;
        lstMoviles = new ArrayList<>();
        modo = "listaVacia";
    }

    public void update() {
        updateCantidadMoviles();
        updateColorFondo();
        lstMoviles.forEach(Movil::update);
    }

    public void draw() {
        lstMoviles.forEach(Movil::draw);
    }

    private void updateCantidadMoviles() {
        if (modo.equals("llenarLista") && lstMoviles.size() < cantidadMovilesMax) llenarLista();
        else if (modo.equals("llenarLista") && lstMoviles.size() == cantidadMovilesMax) modo = "listaLlena";
        else if (modo.equals("vaciarLista")) {
            if (!lstMoviles.isEmpty()) vaciarLista();
            else modo = "listaVacia";
        }
        running = !lstMoviles.isEmpty();
    }

    private void updateColorFondo() {
        if (!running) {
            colorFondo = colorOff;
            return;
        }
        alfaFondo = (int) PApplet.map(lstMoviles.size(), 0, cantidadMovilesMax, alfaFondoMax, alfaFondoMin);
        float f = PApplet.map(alfaFondo, alfaFondoMax, alfaFondoMin, 0, 1);
        colorOn = sketch.color(sketch.red(colorOn), sketch.green(colorOn), sketch.blue(colorOn), alfaFondo);
        colorOff = sketch.color(sketch.red(colorOff), sketch.green(colorOff), sketch.blue(colorOff), alfaFondo);
        colorFondo = sketch.lerpColor(colorOff, colorOn, f);
    }

    private void llenarLista() {
        Movil m = new Movil(sketch);
        m.setImg(img);
        m.setDiametro(sketch.random(diametroMin, diametroMax));
        m.setColorRelleno(colorOn);
        m.setDesviacionMax(desviacionMax);
        m.setPeriodo(periodo);
        m.setRangoAlfa(alfaMin, alfaMax);
        m.setVelocidadNoise(velNoise);
        m.setDeltaIndPos((int) sketch.random(dIndPosMin, dIndPosMax));
        m.setLstPos(posiciones);
        m.setLstNormal(normales);
        m.setup();
        lstMoviles.add(m);
    }

    private void vaciarLista() {
        for (int i = lstMoviles.size() - 1; i >= 0; i--) {
            if (!lstMoviles.get(i).isFinRecorrido()) {
                continue;
            }
            lstMoviles.remove(i);
        }
    }

    public void actualizarVelocidadNoisePorDiferencial(String modo) {
        float d = modo.equals("+") ? dVelNoise : -dVelNoise;
        velNoise += d;
        velNoise = constrain(velNoise, velNoiseMin, velNoiseMax);
        lstMoviles.forEach(m -> m.setVelocidadNoise(velNoise));
    }

    public void setVelocidadNoise(float f) {
        velNoise = PApplet.map(f, 0, 1, velNoiseMin, velNoiseMax);
        velNoise = constrain(velNoise, velNoiseMin, velNoiseMax);
        lstMoviles.forEach(m -> m.setVelocidadNoise(velNoise));
    }

    public void conmutarEstadoFluido() {
        if (modo.equals("listaVacia") || modo.equals("vaciarLista")) modo = "llenarLista";
        else if (modo.equals("listaLlena") || modo.equals("llenarLista")) modo = "vaciarLista";
    }

    // <editor-fold defaultstate="collapsed" desc="*** setter & getter ***">
    public void setImg(PImage img) {
        this.img = img;
    }

    public void setCantidadMovilesMax(int cantidadMovilesMax) {
        this.cantidadMovilesMax = cantidadMovilesMax;
    }

    public void setDesviacionMax(float desviacionMax) {
        this.desviacionMax = desviacionMax;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public void setNormales(List<PVector> normales) {
        this.normales = normales;
    }

    public void setPosiciones(List<PVector> posiciones) {
        this.posiciones = posiciones;
    }

    public void setRangoVelocidadNoise(float velNoiseMin, float velNoiseMax, float dVelNoise) {
        this.velNoiseMin = velNoiseMin;
        this.velNoiseMax = velNoiseMax;
        this.dVelNoise = dVelNoise;
        this.velNoise = velNoiseMin;
    }

    public void setRangoDiametro(float diametroMin, float diametroMax) {
        this.diametroMin = diametroMin;
        this.diametroMax = diametroMax;
    }

    public void setRangoAlfaMovil(float alfaMin, float alfaMax) {
        this.alfaMin = alfaMin;
        this.alfaMax = alfaMax;
    }

    public void setRangoAlfaFondo(float alfaFondoMin, float alfaFondoMax) {
        this.alfaFondoMin = alfaFondoMin * 255;
        this.alfaFondoMax = alfaFondoMax * 255;
        alfaFondo = alfaFondoMax;
    }

    public void setRangDeltaIndPos(int dIndPosMin, int dIndPosMax) {
        this.dIndPosMin = dIndPosMin;
        this.dIndPosMax = dIndPosMax;
    }

    public void setRangoColores(int colorOff, int colorOn) {
        this.colorOff = colorOff;
        this.colorOn = colorOn;
        colorFondo = colorOff;
    }

    public int getColorFondo() {
        return colorFondo;
    }
// </editor-fold>
}