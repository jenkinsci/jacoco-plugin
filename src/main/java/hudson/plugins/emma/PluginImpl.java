package hudson.plugins.emma;

import hudson.Plugin;
import hudson.tasks.Publisher;

/**
 * Entry point of the plugin.
 *
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {
    @Override
    public void start() throws Exception {
        Publisher.PUBLISHERS.addRecorder(EmmaPublisher.DESCRIPTOR);
    }
}
