const TREE_ICON_MAP = {
    'specificationTemplate': 'fa fa-file-text-o',
    'objectiveTemplate': 'fa fa-bullseye',
    'stakeholderTemplate': 'fa fa-user',
    'link_specificationTemplate': 'fa fa-file-text-o',
    'link_stakeholderTemplate': 'fa fa-user',
    'link_objectiveTemplate': 'fa fa-bullseye',
    'templateLink': 'fa fa-chain'
};

function createTreeEntity(item) {
    const treeEntity = {
        'text': item.name,
        'icon': TREE_ICON_MAP[item.type],
        'data': item
    };
    return treeEntity;
}

function jsTreeDataLoader(obj, callback) {
    if (obj.id === '#') {
        callback([
            {
                'text': 'Specification Templates',
                'id': 'specificationTemplates',
                'data': {
                    'type': 'specificationTemplates'
                },
                'children': true
            },
            {
                'text': 'Stakeholder Templates',
                'id': 'stakeholderTemplates',
                'data': {
                    'type': 'stakeholderTemplates'
                },
                'children': true
            },
            {
                'text': 'Objective Templates',
                'id': 'objectiveTemplates',
                'data': {
                    'type': 'objectiveTemplates'
                },
                'children': true
            },
            {
                'text': 'Links',
                'id': 'templateLinks',
                'data': {
                    'type': 'templateLinks'
                },
                'children': true
            }
        ]);
    } else {
        switch (obj.data.type) {
            case 'specificationTemplates':
                fetch('/tree/specificationTemplate')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'specificationTemplate_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'stakeholderTemplates':
                fetch('/tree/stakeholderTemplate')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'stakeholderTemplate_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'objectiveTemplates':
                fetch('/tree/objectiveTemplate')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'objectiveTemplate_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'templateLinks':
                fetch('/tree/link/templateLink')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'templateLink_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
        }
    }
}

function getBaseNodeUrl(node) {
    let url = '/';
    switch (node.data.type) {
        case 'specificationTemplate':
        case 'specificationTemplates':
            url += 'specification/template';
            break;
        case 'stakeholderTemplate':
        case 'stakeholderTemplates':
            url += 'stakeholder/template';
            break;
        case 'objectiveTemplate':
        case 'objectiveTemplates':
            url += 'objective/template';
            break;
        case 'templateLink':
        case 'templateLinks':
            url += 'templateLink';
            break;
    }
    return url;
}

function getNodeUrl(node) {
    let url = getBaseNodeUrl(node);
    url += '/' + node.data.id;
    return url;
}

function createEnterAction(node) {
    let url = getBaseNodeUrl(node);
    url += '/new';
    return {
       'label': 'Enter',
       'action': obj => {
           window.location.href = url;
       }
   };
}

function createFilterAction(node) {
    let url = getBaseNodeUrl(node);
    url += '/find';
    return {
       'label': 'Filter',
       'action': obj => {
           window.location.href = url;
       }
   };
}

function createUpdateAction(node) {
    const url = getNodeUrl(node) + '/edit';
    return {
       'label': 'Update',
       'action': obj => {
           window.location.href = url;
       }
   };
}

function createDeleteAction(name, node) {
    const url = getNodeUrl(node) + '/delete';
    return {
       'label': 'Delete',
       'action': obj => {
            if (confirm('Are you sure you want to delete this ' + name + '?')) {
                const form = $('#commonDeleteForm')[0];
                form.action = url;
                form.submit();
            }
       }
    };
}

$(document).ready(function () {
    $('#tree').jstree({
        'core': {
            'data': jsTreeDataLoader
        },
        'plugins': ['contextmenu'],
        'contextmenu': {
            'select_node': false,
            'items': (node, callback) => {
                switch (node.data.type) {
                    case 'specificationTemplates':
                        callback({
                            'copyComplete': {
                                'label': 'Copy Complete',
                                'action': obj => {
                                    window.location.href = '/specification/template/new?collectionType=templateDeepCopy';
                                }
                            },
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/specification/template/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': createEnterAction(node),
                            'filter': createFilterAction(node)
                        });
                        break;
                    case 'stakeholderTemplates':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/stakeholder/template/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': createEnterAction(node),
                            'filter': createFilterAction(node)
                        });
                        break;
                    case 'objectiveTemplates':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/objective/template/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': createEnterAction(node),
                            'filter': createFilterAction(node)
                        });
                        break;
                    case 'specificationTemplate':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Specification Template', node)
                        });
                        break;
                    case 'stakeholderTemplate':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Stakeholder Template', node)
                        });
                        break;
                    case 'objectiveTemplate':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Objective Template', node)
                        });
                        break;
                    case 'templateLinks':
                        callback({
                            'enter': createEnterAction(node),
                            'filter': createFilterAction(node)
                        });
                        break;
                    case 'templateLink':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Template Link', node)
                        });
                        break;
                }
            }
        }
    });
})

function updateTreeForSpecificationTemplates() {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('specificationTemplates');
    });
}

function updateTreeForStakeholderTemplates() {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('stakeholderTemplates');
    });
}

function updateTreeForObjectiveTemplates() {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('objectiveTemplates');
    });
}

function updateTreeForTemplateLinks() {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('templateLinks');
    });
}

function updateTreeForSpecificationTemplate(specificationTemplateId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('specificationTemplates', () => {
            jsTree.select_node('specificationTemplate_' + specificationTemplateId);
        });
    });
}

function updateTreeForStakeholderTemplate(stakeholderTemplateId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('stakeholderTemplates', () => {
            jsTree.select_node('stakeholderTemplate_' + stakeholderTemplateId);
        });
    });
}

function updateTreeForObjectiveTemplate(objectiveTemplateId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('objectiveTemplates', () => {
            jsTree.select_node('objectiveTemplate_' + objectiveTemplateId);
        });
    });
}

function updateTreeForTemplateLink(templateLinkId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('templateLinks', () => {
            jsTree.select_node('templateLink_' + templateLinkId);
        });
    });
}
