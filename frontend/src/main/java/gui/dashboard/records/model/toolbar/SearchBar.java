package gui.dashboard.records.model.toolbar;

class SearchBar extends MyPanel {
    SearchBar(String[] atts) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new MyLabel("Search" + model.parser.denominator(atts)));
        var searchField = Factory.field(20, Field.TEXT);
        add(searchField);
        add(new MyButton("Search", e -> {
            for (var att : atts) {
                addCriteria(discreteValues, att, searchField.getText());
            }
        }));
    }
}
