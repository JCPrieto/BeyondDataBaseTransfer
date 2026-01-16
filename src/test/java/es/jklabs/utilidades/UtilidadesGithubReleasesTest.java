package es.jklabs.utilidades;

import es.jklabs.json.github.GitHubAsset;
import es.jklabs.json.github.GitHubRelease;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class UtilidadesGithubReleasesTest {

    @Test
    public void normalizaVersion() {
        assertEquals("1.2.3", UtilidadesGithubReleases.normalizarVersion("v1.2.3"));
        assertEquals("2.0.0", UtilidadesGithubReleases.normalizarVersion("V2.0.0-beta"));
        assertEquals("1.0.1", UtilidadesGithubReleases.normalizarVersion(" 1.0.1 "));
        assertEquals("3.4.5", UtilidadesGithubReleases.normalizarVersion("3.4.5-rc.1"));
    }

    @Test
    public void parseaVersiones() {
        assertArrayEquals(new int[]{1, 2, 3}, UtilidadesGithubReleases.parseVersion("1.2.3"));
        assertArrayEquals(new int[]{1, 2, 0}, UtilidadesGithubReleases.parseVersion("1.2"));
        assertArrayEquals(new int[]{1, 0, 3}, UtilidadesGithubReleases.parseVersion("1.x.3"));
        assertArrayEquals(new int[]{0, 0, 0}, UtilidadesGithubReleases.parseVersion("abc"));
    }

    @Test
    public void comparaVersiones() {
        assertTrue(UtilidadesGithubReleases.diferenteVersion("1.3.6"));
        assertFalse(UtilidadesGithubReleases.diferenteVersion("1.3.5"));
        assertFalse(UtilidadesGithubReleases.diferenteVersion("1.2.9"));
    }

    @Test
    public void seleccionaAssetPreferentePorNombre() {
        GitHubAsset esperado = new GitHubAsset();
        esperado.setName("BeyondDataBaseTransfer-1.2.3.zip");
        esperado.setBrowserDownloadUrl("https://example.com/esperado.zip");
        GitHubAsset alternativo = new GitHubAsset();
        alternativo.setName("otro.zip");
        alternativo.setBrowserDownloadUrl("https://example.com/otro.zip");
        GitHubRelease release = new GitHubRelease();
        release.setAssets(Arrays.asList(alternativo, esperado));

        GitHubAsset resultado = UtilidadesGithubReleases.getAssetCompatible(release, esperado.getName());

        assertNotNull(resultado);
        assertEquals(esperado.getName(), resultado.getName());
    }

    @Test
    public void seleccionaPrimerZipSiNoHayNombreEsperado() {
        GitHubAsset primero = new GitHubAsset();
        primero.setName("alpha.zip");
        primero.setBrowserDownloadUrl("https://example.com/alpha.zip");
        GitHubAsset segundo = new GitHubAsset();
        segundo.setName("bravo.zip");
        segundo.setBrowserDownloadUrl("https://example.com/bravo.zip");
        GitHubRelease release = new GitHubRelease();
        release.setAssets(Arrays.asList(primero, segundo));

        GitHubAsset resultado = UtilidadesGithubReleases.getAssetCompatible(release, "inexistente.zip");

        assertNotNull(resultado);
        assertEquals("alpha.zip", resultado.getName());
    }

    @Test
    public void noSeleccionaAssetSiNoHayZip() {
        GitHubAsset asset = new GitHubAsset();
        asset.setName("archivo.tar.gz");
        GitHubRelease release = new GitHubRelease();
        release.setAssets(Collections.singletonList(asset));

        GitHubAsset resultado = UtilidadesGithubReleases.getAssetCompatible(release, "inexistente.zip");

        assertNull(resultado);
    }

    @Test
    public void construyeNombreApp() {
        assertEquals("BeyondDataBaseTransfer-1.2.3.zip", UtilidadesGithubReleases.getNombreApp("1.2.3"));
    }
}
