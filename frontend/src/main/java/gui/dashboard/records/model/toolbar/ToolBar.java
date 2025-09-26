package gui.dashboard.records.model.toolbar;

class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    Map<String,List<Range>> boundedValues = new HashMap<>();
    <T> void addCriteria(Map<String,List<T>> map, String att, T value) {
        map.computeIfAbsent(att, k -> new ArrayList<>()).add(value);
    }

    Model model;
    ToolBar(Model model) {
        this.model = model;

        var searchedTexts = model.reflect.fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(new SearchBar(searchedTexts.toArray(String[]::new)));
        }

        var unique = model.reflect.fields.haveConstraint(Constraints::unique);
        if (unique.size() > 0) {
            add(new MyButton("Search Profile", e -> new SearchProfile(unique.toArray(String[]::new))));
        }

        for (var enumerated : model.reflect.fields.haveConstraint(Constraints::enumerated)) {
            var title =  model.parser.formatName(enumerated);
            add(new MyButton(this, title, e -> new MultipleSelections(title, enumerated)));
        }

        for (var bounded : model.reflect.fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var title =  model.parser.formatName(bounded);
            add(new MyButton(this, title, e -> new RangeSelection(title, bounded)));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Apply", e -> {
            System.out.println(discreteValues.values());
            System.out.println(boundedValues.values());
        }));
    }
}
