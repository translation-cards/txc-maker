function TextInputFieldController() {

}

angular.module('txcmaker').component('textInputField', {
  templateUrl: 'components/TextInputField/textInputField.html',
  controller: TextInputFieldController,
  bindings: {
    label: '<',
    name: '<',
    description: '<',
    value: '<',
    onUpdate: '&'
  }
});