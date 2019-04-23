import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { TooltipModule } from './tooltip-module/TooltipModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule,
	TooltipModule
];

export const playerColors = [
  '#CF3232', // red,
  '#00A0AB' // teal
]