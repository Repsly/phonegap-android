function KeyEvent() 
{
}

KeyEvent.prototype.menuTrigger = function()
{
  var e = document.createEvent('Events');
  e.initEvent('menuKeyDown');
  document.dispatchEvent(e);
}

KeyEvent.prototype.searchTrigger= function()
{
  var e = document.createEvent('Events');
  e.initEvent('searchKeyDown');
  document.dispatchEvent(e);
}

KeyEvent.prototype.backTrigger= function()
{
  var e = document.createEvent('Events');
  e.initEvent('backKeyDown');
  document.dispatchEvent(e);
}  

KeyEvent.prototype.forwardTrigger= function()
{
  var e = document.createEvent('Events');
  e.initEvent('forwardKeyDown');
  document.dispatchEvent(e);
}

if (document.keyEvent == null || typeof document.keyEvent == 'undefined')
{
  window.keyEvent = document.keyEvent = new KeyEvent();
}
