package net.cdahmedeh.poetwrite.query.holder;

import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.query.steps.RootQueryStep;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Owns the autocomplete tree so the ViewController does not have to.
 *
 * Building walks every step class and can block, so tree() is expected to be
 * called from the TaskBus -- see MainViewController.requestAutoComplete().
 *
 * TODO: The cached tree keeps the parameters accumulated on the root and on
 *       RhymeWithQueryStep from the previous invocation. Deeper levels are
 *       rebuilt by getSteps() each time so they start clean, but these two do
 *       not. Either rebuild per request, or clear the parameters here, once
 *       it is clear which the wizard wants.
 */
@Singleton
public class AutoCompleteTreeHolder {

    private final RootQueryStep rootQueryStep;

    private QueryStep tree = null;

    @Inject
    public AutoCompleteTreeHolder(RootQueryStep rootQueryStep) {
        this.rootQueryStep = rootQueryStep;
    }

    /** Blocking on the first call. Built once, then handed back. */
    public QueryStep tree() {
        if (tree == null) {
            tree = rootQueryStep.build();
        }
        return tree;
    }
}