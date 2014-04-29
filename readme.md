# Business Entity Linker

DPU for [UnifiedViews](https://github.com/UnifiedViews/Core)/[ODCS](https://github.com/mff-uk/ODCS) for deduplication of business entities by [Silk](https://www.assembla.com/spaces/silk/wiki/).

The DPU is a wrapper around Silk which is by narrowing deduplication use cases able to provide reasonable GUI for most common situations:

   - Linking by exact identifier comparison (`(schema|gr):(vatID|taxID)`, `adms:identifier/skos:notation` etc.)
   - Linking based on name (`(schema|gr):(name|legalName)` etc.) similarity measured by one character-based distance measures supported by Silk (Levenshtein, Jaro, Jaro-Winkler)
   - Linking based on name similarity supplemented by geocoordinates distance

## Modes

Business Entity Linker (BEL) can be used within UnifiedViews in the position of a transformer or an extractor. When used as a transformer, input data units are dumped to file (prior filtering of unused properties might greatly reduce the size file) and those files are given to Silk. This mode is reasonable only for smaller datasets. When used as an extractor, SPARQL Endpoint parameters must be specified and Silk takes care of reading the data. (Be aware of data store limits when Silk falls back to querying with `ORDER BY` clause, more on [Silk mailing list](https://groups.google.com/d/msg/silk-discussion/9C6_OVAMI_g/owOu-qRJBoQJ).) The modes can be combined as well (e.g. in an incremental pipeline when the newly extracted data is small but needs to be linked against existing dataset).

BEL also supports self-linking in which case all properties must be entered only once and all links between A -> A are excluded from the result. Due to Silk's implementation both pairs A -> B and B -> A are generated.

## Settings

Settings consists of three parts. First specifies mode - one or two datasets, read from data unit or from SPARQL Endpoint. Second, largest part specifies the rule that gets generated. First you need to specify class of resources that will be compared. Several predefined options are available, but you can enter your own (this enables you to use `FILTER`). Then can choose between linking by identifiers (exact string match) and approximate name comparison, additionally the geo comparison can be turned on. You have to select identifier and name properties from available options, the property path (in Silk's syntax) is a text field. In case you really need another property, you need to add the option o `OptionsList` class and recompile. (Alternative text field isn't available due to dialog dimensions and complexity.)

When using approximate name comparison the metric can be selected (only character-based metrics are available because token-based don't make sense and perform poorly for names of business entities) and the threshold. These properties have the same interpretation as in Silk documentation the generated rule uses them directly. All names are transformed to lower-case and "special" characters are removed.

If you add geocoordinates information, this comparison is aggregated with name comparison as a weighted average. Geo has fixed weight=1, you can select the weight of name similarity measure. Generally values in range 2-4 performed well.

Distance threshold can be set in km only. It's hard to recommend any specific value, because Silk handles the situation when the geocoordinates aren't present as if it should compare only by name (i.e. no penalty is included and overall confidence score is the same as name similarity score) and more importantly it employs the same behaviour when the distance exceeds threshold × 2. For example assume the name similarity score is perfect, the weights are 1 : 1 and you have these pairs:

Distance as a fraction of the threshold | Name similarity score | Distance score | Overall score
:-------------------------------------: | :-------------------: | :------------: | :-----------:
0 | 1 | 1.0 | 1.00
1/2 | 1 | 0.5 | 0.75
1 | 1 | 0.0 | 0.50
2 | 1 | -1.0 | 0.00
>2 | 1 | - | **1.00**
unknown | 1 | - | **1.00**

Combined with linearity of the distance measure the correct setting is hard to achieve. When you use low threshold, many pairs will have distance larger than double that and will be compared only by name. When you set high threshold the difference between "around the corner" and "next city over" in the score is small. More about this on [Silk mailing list](https://groups.google.com/d/msg/silk-discussion/nQ4H9Pc-KGA/UZzvv8XMW6cJ).

Confidence cutoff is specifies score which will divide generated links into two data units that can be dealt with differently. Blocks option specifies the `Blocking` parameter which has great impact on the performance.

Last tab of setting is used for configuring path to silk.jar and memory limit for the process. Even though Silk provides API which can be used directly, it's (at least with my abilities) impossible to create an OSGi bundle from Silk JAR that would work with UnifiedViews (UnifiedViews deals with the issue in similar fashion). If you can make it run, please let me know.

## Notes

Be aware of the memory and CPU required for Silk linking task. Exact comparison of identifiers is roughly 20× faster than name-based comparison. Adding coordinates into the mix, slows the process by factor of 3-4. If you are dealing large tasks (in terms of cartesian product of entities in source datasets) you might want to run it in smaller batches (e.g. use property like country to group filter entities for each run) even outside of UnifiedViews.