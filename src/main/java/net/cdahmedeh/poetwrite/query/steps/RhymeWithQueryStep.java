package net.cdahmedeh.poetwrite.query.steps;

import lombok.Getter;
import lombok.Setter;
import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.services.EditorStatusHolder;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RhymeWithQueryStep extends QueryStep {
    EditorStatusHolder editorStatusHolder;

    public RhymeWithQueryStep(TaskBus taskBus) {
        super("rhyme with", taskBus);
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                step("previous line")
                        .steps(relationshipsSteps()),
                step("next line")
                        .steps(relationshipsSteps()),
                step("matching pattern").preview(PatternGroupPreview::new)
                        .command(this::findPatternGroups)
                        .steps(relationshipsSteps())
        ));
    }

    private Supplier<List<QueryStep>> relationshipsSteps() {
        return () -> List.of(
                step("means")
                        .search(DictionarySearch::new).preview(WordPreview::new),
                step("related to")
                        .search(DictionarySearch::new).preview(WordPreview::new),
                step("sounds like")
                        .search(DictionarySearch::new).preview(WordPreview::new)
        );
    }

    private List<QueryStep> findPatternGroups(QueryParameters parameters) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<QueryStep> steps = new ArrayList<>();
        steps.add(new PatternGroupStep("A (less)"));
        steps.add(new PatternGroupStep("B (tion)"));
        steps.add(new PatternGroupStep("C (able)"));
        return steps;
    }

    public static class PatternGroupParameter extends QueryParameter {
        @Getter @Setter
        String pattern = null;
    }

    public class PatternGroupStep extends QueryStep {
        protected PatternGroupStep(String name) {
            super(name, RhymeWithQueryStep.this.taskBus);
        }
    }

    public static class PatternGroupPreview extends QueryPreview {
        @Getter @Setter
        String preview = null;
    }

    public static class DictionarySearch extends QuerySearch {
        @Getter @Setter
        String search = null;
    }

    public static class WordPreview extends QueryPreview {
        @Getter @Setter
        String word = null;

        @Getter @Setter
        String arpaBet = null;
    }


}
