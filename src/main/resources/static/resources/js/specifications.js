const TREE_ICON_MAP = {
    'specification': 'fa fa-file-text-o',
    'specificationObjective': 'fa fa-bullseye',
    'stakeholder': 'fa fa-user',
    'stakeholderObjective': 'fa fa-check-square-o'
};
const DEFAULT_MESSAGE = 'Please select an item in Entity Explorer';

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
        callback([{
            'text': 'Specifications',
            'state': {
                'opened': true
            },
            'data': {
                'type': 'specifications'
            },
            'children': true
        }]);
    } else {
        switch (obj.data.type) {
            case 'specifications':
                fetch('/tree/specification')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'specification_' + item.id;
                            treeEntity.children = [
                                {
                                    'text': 'Objectives',
                                    'id': 'specification_' + item.id + '_specificationObjectives',
                                    'data': {
                                        'type': 'specificationObjectives',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                },
                                {
                                    'text': 'Stakeholders',
                                    'id': 'specification_' + item.id + '_stakeholders',
                                    'data': {
                                        'type': 'stakeholders',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                }
                            ];
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'specificationObjectives':
                fetch('/tree/specification/' + obj.data.specificationId + '/specificationObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'specificationObjective_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'stakeholders':
                fetch('/tree/specification/' + obj.data.specificationId + '/stakeholder')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'stakeholder_' + item.id;
                            treeEntity.children = true;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'stakeholder':
                fetch('/tree/stakeholder/' + obj.data.id + '/stakeholderObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.id = 'stakeholderObjective_' + item.id;
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
        }
    }
}

function getNodeUrl(node) {
    let url = '/' + node.data.type + '/' + node.data.id;
    for (let i = 0; i < node.parents.length; i ++) {
        const parentData = $('#tree').jstree().get_node(node.parents[i]).data;
        if (parentData && parentData.id) {
            url = '/' + parentData.type + '/' + parentData.id + url;
        }
    }
    return url;
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
                    case 'specifications':
                        callback({
                            'createComplete': {
                                'label': 'Create Complete',
                                'action': obj => {
                                    window.location.href = '/specification/new?collectionType=templateDeepCopy';
                                }
                            },
                            'copyComplete': {
                                'label': 'Copy Complete',
                                'action': obj => {
                                    window.location.href = '/specification/new?collectionType=srcSpecification';
                                }
                            },
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/specification/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    window.location.href = '/specification/new';
                                }
                            },
                            'filter': {
                                'label': 'Filter',
                                'action': obj => {
                                    window.location.href = '/specification/find';
                                }
                            }
                        });
                        break;
                    case 'specificationObjectives':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/specification/' + node.data.specificationId + '/specificationObjective/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    window.location.href = '/specification/' + node.data.specificationId + '/specificationObjective/new';
                                }
                            }
                        });
                        break;
                    case 'stakeholders':
                        callback({
                            'copyTemplate': {
                                'label': 'Copy Template',
                                'action': obj => {
                                    window.location.href = '/specification/' + node.data.specificationId + '/stakeholder/new?collectionType=copyTemplate';
                                }
                            },
                            'enter': {
                                'label': 'Enter',
                                'action': obj => {
                                    window.location.href = '/specification/' + node.data.specificationId + '/stakeholder/new';
                                }
                            }
                        });
                        break;
                    case 'specification':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Specification', node)
                        });
                        break;
                    case 'specificationObjective':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Specification Objective', node)
                        });
                        break;
                    case 'stakeholder':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Stakeholder', node),
                            'addObjective': {
                                'label': 'Add Objective',
                                'action': obj => {
                                    window.location.href = '/specification/' + node.data.specificationId + '/stakeholder/' + node.data.id + '/stakeholderObjective/new';
                                }
                            }
                        });
                        break;
                    case 'stakeholderObjective':
                        callback({
                            'update': createUpdateAction(node),
                            'delete': createDeleteAction('Stakeholder Objective', node)
                        });
                        break;
                }
            }
        }
    });
})

function updateTreeForSpecification(specificationId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.select_node('specification_' + specificationId);
    });
}

function updateTreeForSpecificationObjective(specificationId, specificationObjectiveId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('specification_' + specificationId);
        jsTree.open_node('specification_' + specificationId + '_specificationObjectives', () => {
            jsTree.select_node('specificationObjective_' + specificationObjectiveId);
        });
    });
}

function updateTreeForStakeholder(specificationId, stakeholderId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('specification_' + specificationId);
        jsTree.open_node('specification_' + specificationId + '_stakeholders', () => {
            jsTree.select_node('stakeholder_' + stakeholderId);
        });
    });
}

function updateTreeForStakeholderObjective(specificationId, stakeholderId, stakeholderObjectiveId) {
    $('#tree').on('ready.jstree', (e, data) => {
        const jsTree = $('#tree').jstree();
        jsTree.open_node('specification_' + specificationId);
        jsTree.open_node('specification_' + specificationId + '_stakeholders', () => {
            jsTree.open_node('stakeholder_' + stakeholderId, () => {
                jsTree.select_node('stakeholderObjective_' + stakeholderObjectiveId);
            });
        });
    });
}