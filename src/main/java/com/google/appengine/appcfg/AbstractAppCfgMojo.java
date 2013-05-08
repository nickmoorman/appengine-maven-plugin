/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 */
package com.google.appengine.appcfg;

import com.google.appengine.SdkResolver;
import com.google.appengine.tools.admin.AppCfg;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for supporting appcfg commands.
 *
 * @author Matt Stephenson <mattstep@google.com>
 */
public abstract class AbstractAppCfgMojo extends AbstractMojo {

  /**
   * The entry point to Aether, i.e. the component doing all the work.
   *
   * @component
   */

  protected RepositorySystem repoSystem;

  /**
   * The current repository/network configuration of Maven.
   *
   * @parameter default-value="${repositorySystemSession}"
   * @readonly
   */
  protected RepositorySystemSession repoSession;

  /**
   * The project's remote repositories to use for the resolution of project dependencies.
   *
   * @parameter default-value="${project.remoteProjectRepositories}"
   * @readonly
   */
  protected List<RemoteRepository> projectRepos;

  /**
   * The project's remote repositories to use for the resolution of plugins and their dependencies.
   *
   * @parameter default-value="${project.remotePluginRepositories}"
   * @readonly
   */
  protected List<RemoteRepository> pluginRepos;

  /**
   * The server to connect to.
   *
   * @parameter expression="${appengine.server}"
   */
  protected String server;

  /**
   * The username to use.
   *
   * @parameter expression="${appengine.email}"
   */
  protected String email;

  /**
   * Override for the Host header setn with all RPCs.
   *
   * @parameter expression="${appengine.host}"
   */
  protected String host;

  /**
   * Proxies requests through the given proxy server. If --proxy_https is also set, only HTTP will
   * be proxied here, otherwise both HTTP and HTTPS will.
   *
   * @parameter expression="${appengine.proxyHost}"
   */
  protected String proxyHost;

  /**
   * Proxies HTTPS requests through the given proxy server.
   *
   * @parameter expression="${appengine.proxyHttps}"
   */
  protected String proxyHttps;

  /**
   * Do not save/load access credentials to/from disk.
   *
   * @parameter expression="${appengine.noCookies}"
   */
  protected boolean noCookies;

  /**
   * Always read the login password from stdin.
   *
   * @parameter expression="${appengine.passin}"
   */
  protected boolean passin;

  /**
   * Do not use HTTPS to communicate with the Admin Console.
   *
   * @parameter expression="${appengine.insecure}"
   */
  protected boolean insecure;

  /**
   * Override application id from appengine-web.xml or app.yaml.
   *
   * @parameter expression="${appengine.appId}"
   */
  protected String appId;

  /**
   * Override version from appengine-web.xml or app.yaml.
   *
   * @parameter expression="${appengine.version}"
   */
  protected String version;

  /**
   * Use OAuth2 instead of password auth.  Defaults to true.
   *
   * @parameter default-value=true expression="${appengine.oauth2}"
   */
  protected boolean oauth2;

  /**
   * Use the App Engine Java 6 runtime for this app.
   *
   * @parameter expression="${appengine.useJava6}"
   */
  protected boolean useJava6;

  /**
   * Split large jar files (> 10M) into smaller fragments.
   *
   * @parameter expression="${appengine.enableJarSplitting}"
   */
  protected boolean enableJarSplitting;

  /**
   * When --enable-jar-splitting is set, files that match the list of comma separated SUFFIXES will
   * be excluded from all jars.
   *
   * @parameter expression="${appengine.jarSplittingExcludes}"
   */
  protected String jarSplittingExcludes;

  /**
   * Do not delete temporary (staging) directory used in uploading.
   *
   * @parameter expression="${appengine.retainUploadDir}"
   */
  protected boolean retainUploadDir;

  /**
   * The character encoding to use when compiling JSPs.
   *
   * @parameter expression="${appengine.compileEncoding}"
   */
  protected boolean compileEncoding;

  /**
   * Number of days worth of log data to get. The cut-off point is midnight UTC. Use 0 to get all
   * available logs. Default is 1.
   *
   * @parameter expression="${appengine.numDays}"
   */
  protected Integer numDays;

  /**
   * Severity of app-level log messages to get. The range is 0 (DEBUG) through 4 (CRITICAL). If
   * omitted, only request logs are returned.
   *
   * @parameter expression="${appengine.severity}"
   */
  protected String severity;

  /**
   * Append to existing file.
   *
   * @parameter expression="${appengine.append}"
   */
  protected boolean append;

  /**
   * Number of scheduled execution times to compute.
   *
   * @parameter expression="${appengine.numRuns}"
   */
  protected Integer numRuns;

  /**
   * Force deletion of indexes without being prompted.
   *
   * @parameter expression="${appengine.force}"
   */
  protected boolean force;

  /**
   * The name of the backend to perform actions on.
   *
   * @parameter expression="${appengine.backendName}"
   */
  protected String backendName;

  /**
   * Delete the JSP source files after compilation.
   *
   * @parameter expression="${appengine.deleteJsps}"
   */
  protected boolean deleteJsps;

  /**
   * Jar the WEB-INF/classes content.
   *
   * @parameter expression="${appengine.enableJarClasses}"
   */
  protected boolean enableJarClasses;

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  protected void executeAppCfgCommand(String action, String appDir)
      throws MojoExecutionException {
    ArrayList<String> arguments = collectParameters();

    arguments.add(action);
    arguments.add(appDir);

    try {
      AppCfg.main(arguments.toArray(new String[arguments.size()]));
    } catch (Exception ex) {
      throw new MojoExecutionException("Error executing appcfg command="
          + arguments, ex);
    }
  }

  protected void executeAppCfgBackendsCommand(String action, String appDir)
      throws MojoExecutionException {
    ArrayList<String> arguments = collectParameters();

    arguments.add("backends");
    arguments.add(action);
    arguments.add(appDir);
    arguments.add(backendName);
    try {
      AppCfg.main(arguments.toArray(new String[arguments.size()]));
    } catch (Exception ex) {
      throw new MojoExecutionException("Error executing appcfg command="
          + arguments, ex);
    }
  }

  private ArrayList<String> collectParameters() {
    ArrayList<String> arguments = new ArrayList<String>();

    if (server != null && !server.isEmpty()) {
      arguments.add("-s");
      arguments.add(server);
    }

    if (email != null && !email.isEmpty()) {
      arguments.add("-e");
      arguments.add(email);
    }

    if (host != null && !host.isEmpty()) {
      arguments.add("-H");
      arguments.add(host);
    }

    if (proxyHost != null && !proxyHost.isEmpty()) {
      arguments.add("--proxy_host");
      arguments.add(proxyHost);
    }

    if (proxyHttps != null && !proxyHttps.isEmpty()) {
      arguments.add("--proxy_https");
      arguments.add(proxyHttps);
    }

    if (noCookies) {
      arguments.add("--no_cookies");
    }

    if (passin) {
      arguments.add("--passin");
    }

    if (insecure) {
      arguments.add("--insecure");
    }

    if (appId != null && !appId.isEmpty()) {
      arguments.add("-A");
      arguments.add(appId);
    }

    if (version != null && !version.isEmpty()) {
      arguments.add("-V");
      arguments.add(version);
    }

    if (oauth2) {
      arguments.add("--oauth2");
    }

    if (useJava6) {
      arguments.add("--use_java6");
    }

    if (enableJarSplitting) {
      arguments.add("--enable_jar_splitting");
    }

    if (jarSplittingExcludes != null && !jarSplittingExcludes.isEmpty()) {
      arguments.add("--jar_splitting_excludes");
      arguments.add(jarSplittingExcludes);
    }

    if (retainUploadDir) {
      arguments.add("--retain_upload_dir");
    }

    if (compileEncoding) {
      arguments.add("--compile_encoding");
    }

    if (numDays != null) {
      arguments.add("--num_days");
      arguments.add(numDays.toString());
    }

    if (severity != null && !severity.isEmpty()) {
      arguments.add("--severity");
      arguments.add(severity);
    }

    if (append) {
      arguments.add("-a");
    }

    if (numRuns != null) {
      arguments.add("--num_runs");
      arguments.add(numRuns.toString());
    }

    if (force) {
      arguments.add("-f");
    }

    if (deleteJsps) {
      arguments.add("--delete_jsps");
    }

    if (enableJarClasses) {
      arguments.add("--enable_jar_classes");
    }

    return arguments;
  }

  protected void resolveAndSetSdkRoot() throws MojoExecutionException {

    File sdkBaseDir = SdkResolver.getSdk(project, repoSystem, repoSession, pluginRepos, projectRepos);

    try {
      System.setProperty("appengine.sdk.root", sdkBaseDir.getCanonicalPath());
    } catch (IOException e) {
      throw new MojoExecutionException("Could not open SDK zip archive.", e);
    }
  }
}